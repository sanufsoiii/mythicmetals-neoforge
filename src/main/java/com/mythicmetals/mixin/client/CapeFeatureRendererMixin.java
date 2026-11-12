package com.mythicmetals.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mythicmetals.armor.MythicArmor;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.client.models.StarPlatCloakModel;
import com.mythicmetals.armor.CelestiumElytra;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeFeatureRendererMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CapeFeatureRendererMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack ms, MultiBufferSource vertices, int light, AbstractClientPlayer player, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        // Fabric: LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER (no NeoForge equivalent). Fabric API checks it for the
        // whole cape layer (vanilla capes included); the only listener Mythic Metals registered was
        // "!CelestiumElytra.isWearing(player)", so that check is applied here and suppresses any cape.
        if (CelestiumElytra.isWearing(player)) {
            ci.cancel();
            return;
        }
        if (!player.isModelPartShown(PlayerModelPart.CAPE) || player.getSkin().capeTexture() != null) return;

        // Custom Hallowed Cape when no other cape is present
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == MythicArmor.HALLOWED.getChestplate().asItem()) {
            mythicmetals$renderHallowedCape(ms, vertices, light, player, f, g, h, j, k, l);
            ci.cancel();
            return;
        }
        // Custom Star Platinum Cloak when no other cape is present
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == MythicArmor.STAR_PLATINUM.getChestplate().asItem()) {
            mythicmetals$renderStarPlatCape(ms, vertices, light, player, f, g, h, j, k, l);
            ci.cancel();
        }
    }

    @Unique
    private void mythicmetals$renderStarPlatCape(PoseStack ms, MultiBufferSource vertices, int light, AbstractClientPlayer player, float f, float g, float h, float j, float k, float l) {
        double x = Mth.rotLerp(h, (float) player.xCloakO, (float) player.xCloak)
            - Mth.rotLerp(h, (float) player.xo, (float) player.getX());
        double y = Mth.rotLerp(h, (float) player.yCloakO, (float) player.yCloak)
            - Mth.rotLerp(h, (float) player.yo, (float) player.getY());
        double z = Mth.rotLerp(h, (float) player.zCloakO, (float) player.zCloak)
            - Mth.rotLerp(h, (float) player.zo, (float) player.getZ());
        float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
        double o = Mth.sin(yaw * (float) (Math.PI / 180.0));
        double p = -Mth.cos(yaw * (float) (Math.PI / 180.0));
        float q = (float) y * 10.0F;
        q = Mth.clamp(q, -6.0F, 32.0F);
        float r = (float) (x * o + z * p) * 100.0F;
        r = Mth.clamp(r, 0.0F, 150.0F);
        float capeZOffset = (float) (x * p - z * o) * 100.0F;
        capeZOffset = Mth.clamp(capeZOffset, -20.0F, 20.0F);
        if (r < 0.0F) {
            r = 0.0F;
        }


        float t = Mth.lerp(h, player.oBob, player.bob);
        q += Mth.sin(Mth.lerp(h, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * t;

        if (player.isCrouching()) {
            q += 25.0F;
        }

        float backCapeRotation = Mth.clamp(6.0F + r / 2.0F + q, -30, 60);
        VertexConsumer vertexConsumer = vertices.getBuffer(RenderType.entitySolid(MythicModelHandler.STAR_PLATINUM_CLOAK));

        // Transform and render the custom cape
        ms.pushPose();
        ms.translate(0, -0.05, 0.0); // Push up and backwards, then rotate
        ms.mulPose(Axis.XP.rotationDegrees(backCapeRotation));
        ms.mulPose(Axis.ZP.rotationDegrees(capeZOffset / 2.0F));
        ms.mulPose(Axis.YP.rotationDegrees(180.0F - capeZOffset / 1.25F));
        ms.translate(0, 0.05, -0.370); // Move back down
        if (player.isCrouching()) {
            ms.translate(0, 0.15, 0.125);
        }


        StarPlatCloakModel.CAPE_MODEL.render(ms, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        ms.popPose();
    }

    // [VanillaCopy] render the cape
    @Unique
    private void mythicmetals$renderHallowedCape(PoseStack ms, MultiBufferSource vertices, int light, AbstractClientPlayer player, float f, float g, float h, float j, float k, float l) {
        ms.pushPose();
        ms.translate(0.0, 0.0, 0.125);
        double x = Mth.lerp(h, player.xCloakO, player.xCloak)
            - Mth.lerp(h, player.xo, player.getX());
        double y = Mth.lerp(h, player.yCloakO, player.yCloak)
            - Mth.lerp(h, player.yo, player.getY());
        double z = Mth.lerp(h, player.zCloakO, player.zCloak)
            - Mth.lerp(h, player.zo, player.getZ());
        float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
        double o = Mth.sin(yaw * (float) (Math.PI / 180.0));
        double p = -Mth.cos(yaw * (float) (Math.PI / 180.0));
        float q = (float) y * 10.0F;
        q = Mth.clamp(q, -6.0F, 32.0F);
        float r = (float) (x * o + z * p) * 100.0F;
        r = Mth.clamp(r, 0.0F, 150.0F);
        float s = (float) (x * p - z * o) * 100.0F;
        s = Mth.clamp(s, -20.0F, 20.0F);
        if (r < 0.0F) {
            r = 0.0F;
        }

        float t = Mth.lerp(h, player.oBob, player.bob);
        q += Mth.sin(Mth.lerp(h, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * t;
        if (player.isCrouching()) {
            q += 25.0F;
        }

        ms.mulPose(Axis.XP.rotationDegrees(6.0F + r / 2.0F + q));
        ms.mulPose(Axis.ZP.rotationDegrees(s / 2.0F));
        ms.mulPose(Axis.YP.rotationDegrees(180.0F - s / 2.0F));
        VertexConsumer vertexConsumer = vertices.getBuffer(RenderType.entityTranslucent(MythicModelHandler.HALLOWED_CAPE));
        this.getParentModel().renderCloak(ms, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        ms.popPose();
    }
}
