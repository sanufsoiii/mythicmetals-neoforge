package com.mythicmetals.mixin.client;

import com.mythicmetals.component.DrillComponent;
import com.mythicmetals.component.MythicDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand hand);

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void mythicmetals$cancelSwingOnActiveMythrilDrill(InteractionHand hand, boolean fromServerPlayer, CallbackInfo ci) {
		if (!this.level().isClientSide()) {
			return;
		}
		var stack = this.getItemInHand(hand);
		var camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		// This can be null, according to #252
		if (camera == null) return;
		if (camera.isDetached() && stack.getOrDefault(MythicDataComponents.DRILL, DrillComponent.DEFAULT).hasFuel()) {
			ci.cancel();
		}
	}
}
