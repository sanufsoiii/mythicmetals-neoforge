package com.mythicmetals.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public class RuniteArmorSet extends ArmorSet {

    public RuniteArmorSet(ArmorMaterial material, int duraMod) {
        super(material, duraMod);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Properties settings) {
        if (slot != ArmorItem.Type.HELMET) return super.makeItem(material, slot, settings);
        return new RuniteArmor(slot, settings);
    }
}
