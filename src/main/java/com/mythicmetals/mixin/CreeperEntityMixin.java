package com.mythicmetals.mixin;

import com.mythicmetals.misc.WasSpawnedFromCreeper;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Creeper.class)
public class CreeperEntityMixin {

    @ModifyVariable(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private AreaEffectCloud mythicmetals$assignDataToCloud(AreaEffectCloud cloud) {
        ((WasSpawnedFromCreeper) cloud).mythicmetals$setSpawnedFromCreeper(true);
        return cloud;
    }
}
