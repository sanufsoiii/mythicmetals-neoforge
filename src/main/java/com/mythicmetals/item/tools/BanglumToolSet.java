package com.mythicmetals.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import java.util.function.Consumer;

public class BanglumToolSet extends ToolSet {

    @Override
    protected PickaxeItem makePickaxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new BanglumPick(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected ShovelItem makeShovel(Tier material, int damage, float speed, Item.Properties settings) {
        return new BanglumShovel(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    public BanglumToolSet(Tier material, int[] damage, float[] speed, Consumer<Item.Properties> settingsProcessor) {
        super(material, damage, speed, settingsProcessor);
    }

}
