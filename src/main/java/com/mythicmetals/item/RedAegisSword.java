package com.mythicmetals.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class RedAegisSword extends SwordItem {
    public RedAegisSword(Tier material, Properties settings) {
        super(material, settings);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.igniteForTicks(320);
        return super.hurtEnemy(stack, target, attacker);
    }
}
