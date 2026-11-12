package com.mythicmetals.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mythicmetals.MythicMetals;
import com.mythicmetals.armor.*;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.block.entity.RegisterBlockEntityTypes;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.client.rendering.*;
import com.mythicmetals.component.*;
import com.mythicmetals.data.MythicTags;
import com.mythicmetals.entity.MythicEntities;
import com.mythicmetals.item.tools.*;
import com.mythicmetals.misc.*;
import com.mythicmetals.mixin.client.WorldRendererInvoker;
import io.wispforest.owo.ui.util.Delta;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
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

@Mod(value = MythicMetals.MOD_ID, dist = Dist.CLIENT)
public class MythicMetalsClient {
    private static final Logger PORT_LOGGER = LoggerFactory.getLogger("mythicmetals-port");
    private static boolean titleScreenReported = false;

    private long lastTime;
    private float time;
    public static ItemDisplayContext mode;

    public MythicMetalsClient(IEventBus modBus) {
        modBus.<EntityRenderersEvent.RegisterLayerDefinitions>addListener(event ->
            MythicModelHandler.init((loc, def) -> event.registerLayerDefinition(loc, () -> def)));

        renderHammerOutline();
        modBus.<RegisterClientExtensionsEvent>addListener(this::registerArmorRenderer);
        modBus.<EntityRenderersEvent.AddLayers>addListener(this::registerSwirlRenderer);

        // Fabric LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER(player -> !CelestiumElytra.isWearing(player)):
        // applied directly in CapeFeatureRendererMixin (NeoForge has no such event).

        modBus.<EntityRenderersEvent.RegisterRenderers>addListener(event -> {
            event.registerEntityRenderer(MythicEntities.PALLADIUM_MINECART_ENTITY_TYPE, PalladiumMinecartRenderer::new);
            event.registerEntityRenderer(MythicEntities.BANGLUM_TNT_MINECART_ENTITY_TYPE, BanglumTntMinecartEntityRenderer::new);
            event.registerEntityRenderer(MythicEntities.BANGLUM_TNT_ENTITY_TYPE, BanglumTntEntityRenderer::new);
            event.registerEntityRenderer(MythicEntities.BANGLUM_NUKE_ENTITY_TYPE, BanglumNukeEntityRenderer::new);
            event.registerEntityRenderer(MythicEntities.STAR_PLATINUM_ARROW_ENTITY_TYPE, StarPlatinumArrowEntityRenderer::new);
            event.registerEntityRenderer(MythicEntities.RUNITE_ARROW_ENTITY_TYPE, RuniteArrowEntityRenderer::new);

            event.registerBlockEntityRenderer(RegisterBlockEntityTypes.ENCHANTED_MIDAS_GOLD_BLOCK, EnchantedMidasBlockEntityRenderer::new);
        });

        modBus.<RegisterColorHandlersEvent.Item>addListener(event ->
            event.register(UsefulSingletonForColorUtil::potionColor, MythicTools.TIPPED_RUNITE_ARROW));

        modBus.<FMLClientSetupEvent>addListener(event -> event.enqueueWork(() -> {
            registerModelPredicates();

            CarmotShieldHudHandler.init();

            ItemBlockRenderTypes.setRenderLayer(MythicBlocks.CARMOT_BELL_BLOCK, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(MythicBlocks.PALLADIUM_RAIL, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(MythicBlocks.AQUARIUM_GLASS, RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(MythicBlocks.KYBER.getStorageBlock(), RenderType.translucent());
        }));

        NeoForge.EVENT_BUS.<ClientTickEvent.Post>addListener(event -> CarmotShieldHudHandler.tick());

        // Isometric Renders armor stand exporter: the mod does not exist on NeoForge 1.21.1 (compat moved to _excluded/).

        registerTooltipCallbacks();

        NeoForge.EVENT_BUS.<ScreenEvent.Opening>addListener(MythicMetalsClient::onScreenOpening);
    }

    // Port boot self-check: reports reaching the title screen and the registered forge controllers.
    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (titleScreenReported || !(event.getNewScreen() instanceof TitleScreen)) return;
        titleScreenReported = true;

        List<ResourceLocation> controllers = BuiltInRegistries.BLOCK.keySet().stream()
            .filter(id -> id.getNamespace().equals("alloy_forgery") && id.getPath().endsWith("_forge_controller"))
            .sorted()
            .toList();

        PORT_LOGGER.info("[mythicmetals-port] TITLE_SCREEN_REACHED");
        PORT_LOGGER.info("[mythicmetals-port] FORGE_CONTROLLERS={} {}", controllers.size(), controllers);

        if (Boolean.getBoolean("mythicmetals.port.exitOnTitle")) {
            scheduleStop(Minecraft.getInstance(), 20);
        }
    }

    private static void scheduleStop(Minecraft client, int ticksLeft) {
        client.execute(() -> {
            if (ticksLeft <= 0) {
                client.stop();
            } else {
                scheduleStop(client, ticksLeft - 1);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void registerSwirlRenderer(EntityRenderersEvent.AddLayers event) {
        // Fabric LivingEntityFeatureRendererRegistrationCallback for EntityType.PLAYER -> every player skin renderer
        for (var skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new PlayerEnergySwirlFeatureRenderer(
                    (RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) playerRenderer,
                    event.getEntityModels()));
            }
        }
    }

    /**
     * Renders the outline of a {@link HammerBase hammer item.}
     */
    private void renderHammerOutline() {
        // Fabric WorldRenderEvents.BLOCK_OUTLINE -> RenderHighlightEvent.Block; "return false" = cancel the vanilla outline
        NeoForge.EVENT_BUS.<RenderHighlightEvent.Block>addListener(event -> {
            if (!mythicmetals$renderHammerOutline(event)) event.setCanceled(true);
        });
    }

    private static boolean mythicmetals$renderHammerOutline(RenderHighlightEvent.Block event) {
        {
            var entity = event.getCamera().getEntity();
            if (!entity.isAlwaysTicking()) return true;
            var player = (Player) entity;
            var outlinePos = event.getTarget().getBlockPos();
            var outlineState = player.level().getBlockState(outlinePos);
            var cameraPos = event.getCamera().getPosition();

            // Only render the outline if you are hovering over something the hammer can break
            var stack = player.getMainHandItem();
            if (stack.getItem() instanceof HammerBase hammer
                && !outlineState.isAir()
                && hammer.isCorrectToolForDrops(stack, outlineState)) {

                var reach = BlockBreaker.getReachDistance(player);
                BlockHitResult blockHitResult = (BlockHitResult) player.pick(reach, 1, false);

                var facing = blockHitResult.getDirection().getOpposite();
                var blocks = BlockBreaker.findBlocks(facing, outlinePos, hammer.getDepth());
                var originalPos = outlinePos;

                // Create VoxelShapes out of the block positions and put them in a list
                var voxels = new ArrayList<VoxelShape>();

                for (BlockPos blockPos : blocks) {
                    var blockState = player.level().getBlockState(blockPos);
                    if (!blockState.isAir() && hammer.isCorrectToolForDrops(stack, blockState)) {
                        voxels.add(blockState.getShape(
                                player.level(),
                                blockPos,
                                CollisionContext.of(entity)
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
                    event.getPoseStack(),
                    event.getMultiBufferSource().getBuffer(RenderType.lines()),
                    outlineShape,
                    originalPos.getX() - cameraPos.x,
                    originalPos.getY() - cameraPos.y,
                    originalPos.getZ() - cameraPos.z,
                    0, 0, 0, 0.4F //RGBA
                );
                // Cancel the event to prevent the middle outline from rendering
                return false;
            }

            // Keep moving along if we reach this point
            return true;
        }
    }

    private void registerArmorRenderer(RegisterClientExtensionsEvent event) {
        Item[] armors = BuiltInRegistries.ITEM.stream()
            .filter(i -> i instanceof HallowedArmor
                && BuiltInRegistries.ITEM.getResourceKey(i).get().location().getNamespace().equals(MythicMetals.MOD_ID))
            .toArray(Item[]::new);

        // Fabric ArmorRenderer -> IClientItemExtensions#getHumanoidArmorModel. The texture comes from
        // HallowedArmor#getArmorTexture (IItemExtension), NeoForge's HumanoidArmorLayer renders the part,
        // the enchantment glint and armor trims like the original renderer did.
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HallowedArmor armor = (HallowedArmor) stack.getItem();
                var model = armor.getArmorModel();
                mythicmetals$copyProperties(original, model);
                return model;
            }
        }, armors);
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> void mythicmetals$copyProperties(HumanoidModel<?> from, HumanoidModel<T> to) {
        ((HumanoidModel<T>) from).copyPropertiesTo(to);
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
        // Fabric ItemTooltipCallback -> ItemTooltipEvent
        NeoForge.EVENT_BUS.<ItemTooltipEvent>addListener(event -> {
            var stack = event.getItemStack();
            var lines = event.getToolTip();
            var type = event.getFlags();
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
