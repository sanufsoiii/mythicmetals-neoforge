package com.mythicmetals.client.rendering;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.armor.CarmotShield;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.misc.RegistryHelper;
import com.mythicmetals.misc.UsefulSingletonForColorUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import static com.mythicmetals.misc.UsefulSingletonForColorUtil.MetalColors.SHIELD_BREAK_COLOR;

import com.mojang.blaze3d.vertex.PoseStack;

public class PlayerEnergySwirlFeatureRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public static final ResourceLocation SWIRL_TEXTURE = RegistryHelper.id("textures/models/carmot_shield.png");

    private final PlayerModel<AbstractClientPlayer> swirlModel;

    public PlayerEnergySwirlFeatureRenderer(
        RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context,
        EntityModelSet loader) {
        super(context);
        this.swirlModel = new PlayerModel<>(loader.bakeLayer(MythicModelHandler.CARMOT_SWIRL), false);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (com.mythicmetals.component.MythicAttachments.CARMOT_SHIELD.get(entity).shouldRenderShield()) {
            var shield = com.mythicmetals.component.MythicAttachments.CARMOT_SHIELD.get(entity);
            float f = entity.tickCount + tickDelta;

            this.swirlModel.prepareMobModel(entity, limbAngle, limbDistance, tickDelta);
            this.getParentModel().copyPropertiesTo(this.swirlModel);
            this.getParentModel().copyPropertiesTo(this.swirlModel);

            var consumer = vertexConsumers.getBuffer(RenderType.energySwirl(SWIRL_TEXTURE, (f * .005f) % 1f, f * .005f % 1f));
            this.swirlModel.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            // Break animation
            if (shield.cooldown > CarmotShield.MAX_COOLDOWN - 30) {
                matrices.scale(1.125f, 1.0625f, 1.125f);
                this.swirlModel.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, SHIELD_BREAK_COLOR);
            } else // Regular animation
                this.swirlModel.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, UsefulSingletonForColorUtil.rainbow());
        }
    }
}
