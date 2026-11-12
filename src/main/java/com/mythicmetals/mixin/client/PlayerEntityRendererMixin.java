package com.mythicmetals.mixin.client;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.armor.CarmotShield;
import com.mythicmetals.client.rendering.StormyxShieldRenderer;
import com.mythicmetals.component.DrillComponent;
import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.item.tools.MythicTools;
import com.mythicmetals.misc.UsefulSingletonForColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mythicmetals.client.rendering.PlayerEnergySwirlFeatureRenderer.SWIRL_TEXTURE;
import static com.mythicmetals.misc.UsefulSingletonForColorUtil.MetalColors.SHIELD_BREAK_COLOR;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {
    /**
     * Renders the Carmot Shield on the players arm
     */
    @Inject(method = "renderHand", at = @At("TAIL"))
    private void mythicmetals$renderShieldArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (com.mythicmetals.component.MythicAttachments.CARMOT_SHIELD.get(player).shouldRenderShield()) {
            final var client = Minecraft.getInstance();
            float f = player.tickCount + (client.isPaused() ? 0 : client.getTimer().getGameTimeDeltaPartialTick(true));

            var shield = com.mythicmetals.component.MythicAttachments.CARMOT_SHIELD.get(player);

            var consumer = vertexConsumers.getBuffer(RenderType.energySwirl(SWIRL_TEXTURE, (f * .005f) % 1f, f * .005f % 1f));
            matrices.scale(1.0625f, 1.0625f, 1.0625f);
            if (shield.cooldown > CarmotShield.MAX_COOLDOWN - 30) {
                sleeve.render(matrices, consumer, light, OverlayTexture.NO_OVERLAY, SHIELD_BREAK_COLOR);
            } else // Regular animation
                sleeve.render(matrices, consumer, light, OverlayTexture.NO_OVERLAY, UsefulSingletonForColorUtil.rainbow());
        }
    }

    /**
     * Renders the Stormyx Shield around the player
     */
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("TAIL"))
    private void mythicmetals$renderRainbowShield(AbstractClientPlayer player, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        var stack = player.getUseItem();
        // Only render if the shield is actively being used
        if (stack.getItem().equals(MythicTools.STORMYX_SHIELD)) {
            matrixStack.pushPose();
            StormyxShieldRenderer.renderRainbowShield(matrixStack, vertexConsumerProvider, i, player);
            matrixStack.popPose();
        }
    }

    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void mythicmetals$mythrilDrillPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        var stack = player.getItemInHand(hand);
        if (stack.getOrDefault(MythicDataComponents.DRILL, DrillComponent.DEFAULT).hasFuel()) {
            cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
        }
    }
}
