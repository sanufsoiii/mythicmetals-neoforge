package com.mythicmetals.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;

public class TidesingerArmorSet extends ArmorSet {

    public TidesingerArmorSet(ArmorMaterial material, int duraMod, Consumer<Item.Properties> settingsProcessor) {
        super(material, duraMod, settingsProcessor);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Properties settings) {
        return new TidesingerArmor(slot, settings);
    }
}
