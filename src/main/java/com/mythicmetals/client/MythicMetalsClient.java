package com.mythicmetals.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mythicmetals.MythicMetals;
import com.mythicmetals.armor.*;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.block.entity.RegisterBlockEntityTypes;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.client.rendering.*;
import com.mythicmetals.compat.IsometricArmorStandExporter;
import com.mythicmetals.component.*;
import com.mythicmetals.data.MythicTags;
import com.mythicmetals.entity.MythicEntities;
import com.mythicmetals.item.tools.*;
import com.mythicmetals.misc.*;
import com.mythicmetals.mixin.client.WorldRendererInvoker;
import io.wispforest.owo.ui.util.Delta;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.ArrayList;

public class MythicMetalsClient implements ClientModInitializer {
    private long lastTime;
    private float time;
    public static ItemDisplayContext mode;

    @Override
    public void onInitializeClient() {
        MythicModelHandler.init((loc, def) -> EntityModelLayerRegistry.registerModelLayer(loc, () -> def));

        renderHammerOutline();
        registerArmorRenderer();
        registerModelPredicates();
        registerSwirlRenderer();

        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(player -> !CelestiumElytra.isWearing(player));

        EntityRendererRegistry.register(MythicEntities.PALLADIUM_MINECART_ENTITY_TYPE, PalladiumMinecartRenderer::new);
        EntityRendererRegistry.register(MythicEntities.BANGLUM_TNT_MINECART_ENTITY_TYPE, BanglumTntMinecartEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.BANGLUM_TNT_ENTITY_TYPE, BanglumTntEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.BANGLUM_NUKE_ENTITY_TYPE, BanglumNukeEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.STAR_PLATINUM_ARROW_ENTITY_TYPE, StarPlatinumArrowEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.RUNITE_ARROW_ENTITY_TYPE, RuniteArrowEntityRenderer::new);

        BlockEntityRenderers.register(RegisterBlockEntityTypes.ENCHANTED_MIDAS_GOLD_BLOCK, EnchantedMidasBlockEntityRenderer::new);

        ColorProviderRegistry.ITEM.register(UsefulSingletonForColorUtil::potionColor, MythicTools.TIPPED_RUNITE_ARROW);

        CarmotShieldHudHandler.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> CarmotShieldHudHandler.tick());

        BlockRenderLayerMap.INSTANCE.putBlock(MythicBlocks.CARMOT_BELL_BLOCK, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MythicBlocks.PALLADIUM_RAIL, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MythicBlocks.AQUARIUM_GLASS, RenderType.translucent());

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), MythicBlocks.KYBER.getStorageBlock());

        if (FabricLoader.getInstance().isModLoaded("isometric-renders")) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                IsometricArmorStandExporter.register(dispatcher);
            });
        }

        registerTooltipCallbacks();
    }

    @SuppressWarnings("unchecked")
    private void registerSwirlRenderer() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType != EntityType.PLAYER) return;
            registrationHelper.register(
                new PlayerEnergySwirlFeatureRenderer(
                    (RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) entityRenderer,
                    context.getModelSet()));
        });
    }

    /**
     * Renders the outline of a {@link HammerBase hammer item.}
     */
    private void renderHammerOutline() {
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            if (!blockOutlineContext.entity().isAlwaysTicking()) return true;
            var player = (Player) blockOutlineContext.entity();

            // Only render the outline if you are hovering over something the hammer can break
            var stack = player.getMainHandItem();
            if (stack.getItem() instanceof HammerBase hammer
                && !blockOutlineContext.blockState().isAir()
                && hammer.isCorrectToolForDrops(stack, blockOutlineContext.blockState())) {

                var reach = BlockBreaker.getReachDistance(player);
                BlockHitResult blockHitResult = (BlockHitResult) player.pick(reach, 1, false);

                var facing = blockHitResult.getDirection().getOpposite();
                var blocks = BlockBreaker.findBlocks(facing, blockOutlineContext.blockPos(), hammer.getDepth());
                var originalPos = blockOutlineContext.blockPos();

                // Create VoxelShapes out of the block positions and put them in a list
                var voxels = new ArrayList<VoxelShape>();

                for (BlockPos blockPos : blocks) {
                    var blockState = player.level().getBlockState(blockPos);
                    if (!blockState.isAir() && hammer.isCorrectToolForDrops(stack, blockState)) {
                        voxels.add(blockState.getShape(
                                worldRenderContext.world(),
                                blockPos,
                                CollisionContext.of(blockOutlineContext.entity())
                            ).move(blockPos.getX() - originalPos.getX(),
                                blockPos.getY() - originalPos.getY(),
                                blockPos.getZ() - originalPos.getZ())
                        );
                    }
                }

                // Combine and render the full shape
                var outlineOptional = voxels.stream().reduce(Shapes::or);
                if (outlineOptional.isEmpty()) return true;

                var outlineShape = outlineOptional.get();

                WorldRendererInvoker.mythicmetals$drawShapeOutline(
                    worldRenderContext.matrixStack(),
                    worldRenderContext.consumers().getBuffer(RenderType.lines()),
                    outlineShape,
                    originalPos.getX() - blockOutlineContext.cameraX(),
                    originalPos.getY() - blockOutlineContext.cameraY(),
                    originalPos.getZ() - blockOutlineContext.cameraZ(),
                    0, 0, 0, 0.4F //RGBA
                );
                // Cancel the event to prevent the middle outline from rendering
                return false;
            }

            // Keep moving along if we reach this point
            return true;
        });
    }

    private void registerArmorRenderer() {
        Item[] armors = BuiltInRegistries.ITEM.stream()
            .filter(i -> i instanceof HallowedArmor
                && BuiltInRegistries.ITEM.getResourceKey(i).get().location().getNamespace().equals(MythicMetals.MOD_ID))
            .toArray(Item[]::new);

        ArmorRenderer renderer = (matrices, vertexConsumer, stack, entity, slot, light, original) -> {

            HallowedArmor armor = (HallowedArmor) stack.getItem();
            var model = armor.getArmorModel();
            var texture = armor.getArmorTexture(stack, slot);
            original.copyPropertiesTo(model);
            ArmorRenderer.renderPart(matrices, vertexConsumer, light, stack, model, texture);

            // Armor trim time
            if (!stack.is(MythicArmor.HALLOWED.getHelmet())) {
                var trimComponent = stack.get(DataComponents.TRIM);
                if (trimComponent != null) {
                    var atlas = Minecraft.getInstance().getTextureAtlas(Sheets.ARMOR_TRIMS_SHEET);
                    TextureAtlasSprite sprite = atlas.apply(slot == EquipmentSlot.LEGS ? trimComponent.innerTexture(armor.getMaterial()) : trimComponent.outerTexture(armor.getMaterial()));
                    VertexConsumer trimVertexConsumer = sprite.wrap(
                        ItemRenderer.getFoilBufferDirect(vertexConsumer, Sheets.armorTrimsSheet(trimComponent.pattern().value().decal()), true, stack.hasFoil())
                    );
                    model.renderToBuffer(matrices, trimVertexConsumer, light, OverlayTexture.NO_OVERLAY);
                }
            }
        };
        ArmorRenderer.register(renderer, armors);
    }

    private void registerModelPredicates() {
        ItemProperties.register(
            MythicTools.LEGENDARY_BANGLUM.getPickaxe(), RegistryHelper.id("is_primed"),
            (stack, world, entity, seed) -> BanglumPick.isCoolingDown(entity, stack) ? 0 : 1
        );

        ItemProperties.register(
            MythicTools.LEGENDARY_BANGLUM.getShovel(), RegistryHelper.id("is_primed"),
            (stack, world, entity, seed) -> BanglumShovel.isCoolingDown(entity, stack) ? 0 : 1
        );

        ItemProperties.register(
            MythicTools.MYTHRIL_DRILL, RegistryHelper.id("is_active"),
            (stack, world, entity, seed) -> stack.getOrDefault(MythicDataComponents.DRILL, DrillComponent.DEFAULT).hasFuel() ? 0 : 1
        );

        registerMidasPredicates(MythicTools.MIDAS_GOLD_SWORD);
        registerMidasPredicates(MythicTools.GILDED_MIDAS_GOLD_SWORD);
        registerMidasPredicates(MythicTools.ROYAL_MIDAS_GOLD_SWORD);

        ItemProperties.registerGeneric(RegistryHelper.id("in_world"), (itemStack, world, livingEntity, i) -> {
            if (mode == null) {
                return 1.0f;
            }

            return mode.equals(ItemDisplayContext.GUI) ? 0.0F : 1.0f;
        });

        ItemProperties.register(MythicTools.STORMYX_SHIELD, RegistryHelper.id("blocking"), new ShieldUsePredicate());

        ItemProperties.registerGeneric(RegistryHelper.id("funny_day"), (stack, world, entity, seed) ->
            (StringUtilsAtHome.isFunnyDay()) ? 1 : 0);

        ItemProperties.register(MythicTools.PLATINUM_WATCH, RegistryHelper.id("time"), (stack, world, entity, seed) -> {
            if (entity == null || entity.level() == null) {
                return 0.0F;
            }
            return this.getTime(entity.level());
        });

    }

    public void registerTooltipCallbacks() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            int index = 1;

            if (stack.is(MythicTags.BONUS_FORTUNE)) {
                lines.add(index, Component.translatable("abilities.mythicmetals.bonus_fortune").withColor(UsefulSingletonForColorUtil.MetalColors.CARMOT.rgb()));
            }

            if (stack.is(MythicTags.BONUS_LOOTING)) {
                lines.add(index, Component.translatable("abilities.mythicmetals.bonus_looting").withColor(UsefulSingletonForColorUtil.MetalColors.CARMOT.rgb()));
            }

            if (lines.size() > 2) {
                index += stack.getEnchantments().size();
            }

            if (stack.has(MythicDataComponents.PROMETHEUM)) {
                var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
                if (type.isAdvanced()) {
                    lines.add(index, Component.translatable("tooltip.prometheum.repaired", component.durabilityRepaired())
                        .withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb())
                    );
                }

                lines.add(index, Component.translatable("tooltip.prometheum.regrowth").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                if (component.isOvergrown()) {
                    lines.add(index, Component.translatable("tooltip.prometheum.overgrown").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                }
                if (EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                    lines.add(index, Component.translatable("tooltip.prometheum.engrained").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                }
            }
        });


    }

    private float getTime(Level world) {
        if (world.getDayTime() != this.lastTime) {
            this.lastTime = world.getDayTime();
            this.time += Delta.compute(
                this.time,
                (world.getDayTime()) / 24000.0f,
                Minecraft.getInstance().getTimer().getGameTimeDeltaTicks() / 2.0f
            );
        }

        return this.time;
    }

    public void registerMidasPredicates(Item item) {
        ItemProperties.register(item, RegistryHelper.id("midas_gold_count"),
            (stack, world, entity, seed) -> {
                int goldCount = stack.getOrDefault(MythicDataComponents.GOLD_FOLDED, GoldFoldedComponent.of(0)).goldFolded();
                return MidasGoldSword.countGold(goldCount);
            });
    }

}
