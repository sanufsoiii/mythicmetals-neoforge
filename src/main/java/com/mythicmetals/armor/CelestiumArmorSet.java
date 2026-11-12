package com.mythicmetals.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;

public class CelestiumArmorSet extends ArmorSet {

    public CelestiumArmorSet(ArmorMaterial material, int duraMod, Consumer<Item.Properties> settingsConsumer) {
        super(material, duraMod, settingsConsumer);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Properties settings) {
        return new CelestiumArmor(material, slot, settings);
    }
}
