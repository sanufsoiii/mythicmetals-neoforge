package com.mythicmetals.effects;

import com.mythicmetals.MythicMetals;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CombustingStatusEffect extends MobEffect {
    public CombustingStatusEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        com.mythicmetals.component.MythicAttachments.COMBUSTION_COOLDOWN.get(entity).setCooldown(500);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
