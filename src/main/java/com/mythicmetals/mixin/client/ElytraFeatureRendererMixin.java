package com.mythicmetals.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mythicmetals.armor.CelestiumElytra;
import com.mythicmetals.armor.MythicArmor;
import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ElytraLayer.class)
public abstract class ElytraFeatureRendererMixin {

    // NeoForge replaces the vanilla "itemstack.is(Items.ELYTRA)" check in render() with ElytraLayer#shouldRender
    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
    private void mythicmetals$canRenderCelestiumElytra(net.minecraft.world.item.ItemStack stack, LivingEntity entity, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && CelestiumElytra.isWearing(entity)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    private ResourceLocation mythicmetals$replaceElytraTexture(ResourceLocation value, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, LivingEntity living) {
        var stack = living.getItemBySlot(EquipmentSlot.CHEST);
        if (!stack.is(MythicArmor.CELESTIUM_ELYTRA)) {
            return value;
        }
        return RegistryHelper.id("textures/models/celestium_elytra.png");
    }
}
