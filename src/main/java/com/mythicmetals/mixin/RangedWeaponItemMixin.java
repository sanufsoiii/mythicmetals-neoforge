package com.mythicmetals.mixin;

import com.mythicmetals.item.tools.MythicTools;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

@Mixin(ProjectileWeaponItem.class)
public abstract class RangedWeaponItemMixin {

    // Increases the velocity of Runite Arrows when shot from Ranged Weapons
    // Also decreases divergence, leading to better accuracy
    @ModifyArgs(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;shootProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;IFFFLnet/minecraft/world/entity/LivingEntity;)V"))
    private void mythicmetals$modifyArrowsForRunite(Args args, ServerLevel world, LivingEntity shooter, InteractionHand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, @Nullable LivingEntity target) {
        boolean shouldModify = false;
        for (var arrow : projectiles) {
            if (arrow.is(MythicTools.RUNITE_ARROW) || arrow.is(MythicTools.TIPPED_RUNITE_ARROW)) {
                shouldModify = true;
                break;
            }
        }
        if (shouldModify) {
            args.set(3, speed * 1.3f);
            args.set(4, divergence * 0.9f);
        }
    }
}
