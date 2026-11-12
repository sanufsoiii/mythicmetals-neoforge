package com.mythicmetals.item;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.misc.RegistryHelper;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class CopperSet {
    private Item nuggetItem = null;
    private Item dustItem = null;

    private static Item.Properties createSettings(Consumer<Item.Properties> settingsProcessor) {
        final var settings = new Item.Properties().group(MythicMetals.TABBED_GROUP).tab(0);
        settingsProcessor.accept(settings);
        return settings;
    }

    public CopperSet() {
        this(settings -> {
        });
    }

    public CopperSet(Consumer<Item.Properties> settingsConsumer) {
        if (MythicMetals.CONFIG.enableNuggets()) {
            this.nuggetItem = makeItem(createSettings(settingsConsumer));
        }
        if (MythicMetals.CONFIG.enableDusts()) {
            this.dustItem = makeItem(createSettings(settingsConsumer));
        }
    }

    public void register(String name) {
        if (nuggetItem != null) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_nugget"), nuggetItem);
        }
        if (dustItem != null) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_dust"), dustItem);
        }
    }

    protected Item makeItem(Item.Properties settings) {
        return new Item(settings);
    }

    public Item getNugget() {
        return nuggetItem;
    }

    public Item getDust() {
        return dustItem;
    }
}
