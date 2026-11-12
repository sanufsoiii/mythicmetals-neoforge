package com.mythicmetals.item;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.misc.RegistryHelper;
import io.wispforest.owo.util.TagInjector;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemSet {
    private final Item ingotItem;
    private Item rawOreItem = null;
    private Item nuggetItem = null;
    private Item dustItem = null;
    private boolean requiresBlasting = false;
    // Used for smelting recipes during datagen
    private final float xp;

    private static Item.Properties createSettings(Consumer<Item.Properties> settingsProcessor) {
        final var settings = new Item.Properties().group(MythicMetals.TABBED_GROUP).tab(0);
        settingsProcessor.accept(settings);
        return settings;
    }

    public ItemSet(float xp) {
        this(false, false, xp, settings -> {
        });
    }

    public ItemSet(float xp, boolean requiresBlasting) {
        this(false, requiresBlasting, xp, settings -> {
        });
    }

    public ItemSet(boolean isAlloy) {
        this(isAlloy, true, 0.1f, settings -> {
        });
    }

    public ItemSet(boolean isAlloy, float xp) {
        this(isAlloy, false, xp, settings -> {
        });
    }

    public ItemSet(boolean isAlloy, float xp, boolean requiresBlasting) {
        this(isAlloy, requiresBlasting, xp, settings -> {
        });
    }

    public ItemSet(boolean isAlloy, boolean requiresBlasting, Consumer<Item.Properties> settingsConsumer) {
        this(isAlloy, requiresBlasting, 0.1f, settingsConsumer);
    }

    public ItemSet(boolean isAlloy, boolean requiresBlasting, float xp, Consumer<Item.Properties> settingsConsumer) {
        this.ingotItem = makeItem(createSettings(settingsConsumer));
        if (!isAlloy) {
            this.rawOreItem = makeItem(createSettings(settingsConsumer));
        }
        if (MythicMetals.CONFIG.enableNuggets()) {
            this.nuggetItem = makeItem(createSettings(settingsConsumer));
        }
        if (MythicMetals.CONFIG.enableDusts()) {
            this.dustItem = makeItem(createSettings(settingsConsumer));
        }
        this.xp = xp;
        this.requiresBlasting = requiresBlasting;
    }

    public void register(String name) {
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_ingot"), ingotItem);
        if (rawOreItem != null) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id("raw_" + name), rawOreItem);
        }
        if (nuggetItem != null) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_nugget"), nuggetItem);
            // Conditionally add nuggets to nuggets tag
            TagInjector.inject(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nuggets"), nuggetItem);
        }
        if (dustItem != null) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_dust"), dustItem);
        }
    }

    public void register(String name, boolean imStarPlatinum) {
        if (imStarPlatinum) {
            Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name), ingotItem);
            if (nuggetItem != null) {
                Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_nugget"), nuggetItem);
            }
            if (dustItem != null) {
                Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_dust"), dustItem);
            }
        } else {
            register(name);
        }

    }

    protected Item makeItem(Item.Properties settings) {
        return new Item(settings);
    }

    public Item getRawOre() {
        return rawOreItem;
    }

    public Item getIngot() {
        return ingotItem;
    }

    public Item getNugget() {
        return nuggetItem;
    }

    public Item getDust() {
        return dustItem;
    }

    public boolean requiresBlasting() {
        return requiresBlasting;
    }

    public float getXp() {
        return this.xp > 0 ? this.xp : 0.0f;
    }
}
