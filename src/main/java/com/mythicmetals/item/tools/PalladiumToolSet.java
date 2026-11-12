package com.mythicmetals.item.tools;

import com.mythicmetals.effects.MythicStatusEffects;
import com.mythicmetals.misc.IsAttackCritical;
import com.mythicmetals.misc.RegistryHelper;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import java.util.function.Consumer;

public class PalladiumToolSet extends ToolSet {
    // TODO - Move to config
    public static final int MAX_HEAT = 6;

    public PalladiumToolSet(Tier material, int[] damage, float[] speed, Consumer<Item.Properties> settingsProcessor) {
        super(material, damage, speed, settingsProcessor);
    }

    @Override
    protected SwordItem makeSword(Tier material, int damage, float speed, Item.Properties settings) {
        return new PalladiumSword(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected AxeItem makeAxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PalladiumAxe(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected PickaxeItem makePickaxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PalladiumPick(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected ShovelItem makeShovel(Tier material, int damage, float speed, Item.Properties settings) {
        return new PalladiumShovel(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected HoeItem makeHoe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PalladiumHoe(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    public static class PalladiumAxe extends AxeItem {
        public PalladiumAxe(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyHeatToTarget(target, attacker);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class PalladiumHoe extends HoeItem {
        public PalladiumHoe(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyHeatToTarget(target, attacker);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class PalladiumPick extends PickaxeItem {
        public PalladiumPick(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyHeatToTarget(target, attacker);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class PalladiumShovel extends ShovelItem {
        public PalladiumShovel(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyHeatToTarget(target, attacker);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class PalladiumSword extends SwordItem {
        public PalladiumSword(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyHeatToTarget(target, attacker);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static void applyHeatToTarget(LivingEntity target, LivingEntity attacker) {
        var effect = RegistryHelper.getEntry(MythicStatusEffects.HEAT);
        if (!target.hasEffect(effect)) {
            target.addEffect(new MobEffectInstance(effect, 100), attacker);
        } else {
            var activeEffect = target.getEffect(effect);
            int amplifier = activeEffect == null ? 0 : activeEffect.getAmplifier();
            if (((IsAttackCritical) attacker).mythicmetals$isCritical()) {
                amplifier += 1;
            } else if (target.getRandom().nextInt(3) == 0) {
                amplifier += 1;
            }

            if (amplifier >= MAX_HEAT) {
                WorldOps.playSound(target.level(), target.position(), SoundEvents.GENERIC_BURN, SoundSource.PLAYERS);
            }
            target.addEffect(new MobEffectInstance(effect, 100 + (20 * amplifier * amplifier), Math.min(amplifier, MAX_HEAT)), attacker);
        }
    }
}
