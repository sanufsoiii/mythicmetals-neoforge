package com.mythicmetals.armor;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.misc.RegistryHelper;
import com.mythicmetals.misc.StringUtilsAtHome;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ArmorSet {

    private final ArmorItem helmet;
    private final ArmorItem chestplate;
    private final ArmorItem leggings;
    private final ArmorItem boots;

    private final List<ArmorItem> items;

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = Map.of(
        ArmorItem.Type.HELMET, 12,
        ArmorItem.Type.CHESTPLATE, 16,
        ArmorItem.Type.LEGGINGS, 15,
        ArmorItem.Type.BOOTS, 13
    );

    public ArmorItem baseArmorItem(ArmorMaterial material, ArmorItem.Type slot, int durabilityModifier, Consumer<Item.Properties> settingsProcessor) {
        final var settings = new Item.Properties()
            .group(MythicMetals.TABBED_GROUP)
            .tab(3)
            .durability(BASE_DURABILITY.get(slot) * durabilityModifier);
        settingsProcessor.accept(settings);
        return this.makeItem(material, slot, settings);
    }

    public ArmorSet(ArmorMaterial material, int durabilityModifier) {
        this(material, durabilityModifier, settings -> {
        });
    }

    public ArmorSet(ArmorMaterial material, int durabilityModifier, Consumer<Item.Properties> settingsProcessor) {
        this.helmet = baseArmorItem(material, ArmorItem.Type.HELMET, durabilityModifier, settingsProcessor);
        this.chestplate = baseArmorItem(material, ArmorItem.Type.CHESTPLATE, durabilityModifier, settingsProcessor);
        this.leggings = baseArmorItem(material, ArmorItem.Type.LEGGINGS, durabilityModifier, settingsProcessor);
        this.boots = baseArmorItem(material, ArmorItem.Type.BOOTS, durabilityModifier, settingsProcessor);
        this.items = List.of(helmet, chestplate, leggings, boots);
    }

    public void register(String name) {
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_helmet"), helmet);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_chestplate"), chestplate);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_leggings"), leggings);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_boots"), boots);
    }

    public void register(String modid, String name) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name + "_helmet"), helmet);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name + "_chestplate"), chestplate);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name + "_leggings"), leggings);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name + "_boots"), boots);
    }

    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Properties settings) {
        return new ArmorItem(getEntry(material), slot, settings);
    }

    public ArmorItem getHelmet() {
        return helmet;
    }

    public ArmorItem getChestplate() {
        return chestplate;
    }

    public ArmorItem getLeggings() {
        return leggings;
    }

    public ArmorItem getBoots() {
        return boots;
    }

    public static Map<ArmorItem.Type, Integer> getBaseDurability() {
        return BASE_DURABILITY;
    }

    public List<ArmorItem> getArmorItems() {
        return items;
    }

    public boolean isInArmorSet(ItemStack stack) {
        return this.getArmorItems().contains(stack.getItem());
    }

    public Holder<ArmorMaterial> getEntry(ArmorMaterial material) {
        return BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material);
    }

    public String getTitlecaseName() {
        return StringUtilsAtHome.toTitleCase(MythicArmor.ARMOR_MAP.inverse().get(this));
    }

    public String getMaterialId() {
        return MythicArmor.ARMOR_MAP.inverse().get(this);
    }
}
