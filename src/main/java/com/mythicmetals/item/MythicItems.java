package com.mythicmetals.item;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.misc.*;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MythicItems implements SimpleFieldProcessingSubject<ItemSet> {

    public static final ItemSet ADAMANTITE = new ItemSet(1.5f, true);
    public static final ItemSet AQUARIUM = new ItemSet(0.7f);
    public static final ItemSet BANGLUM = new ItemSet(0.7f);
    public static final ItemSet BRONZE = new ItemSet(true);
    public static final ItemSet CARMOT = new ItemSet(1.2f);
    public static final ItemSet CELESTIUM = new ItemSet(true, true, settings -> settings.fireResistant().rarity(Rarity.RARE));
    public static final ItemSet DURASTEEL = new ItemSet(true);
    public static final ItemSet HALLOWED = new ItemSet(true, true, settings -> settings.rarity(Rarity.UNCOMMON));
    public static final ItemSet KYBER = new ItemSet(0.7f);
    public static final ItemSet MANGANESE = new ItemSet(0.2f);
    public static final ItemSet METALLURGIUM = new ItemSet(true, true, settings -> settings.fireResistant().rarity(Rarity.RARE));
    public static final ItemSet MIDAS_GOLD = new ItemSet(1.0f);
    public static final ItemSet MYTHRIL = new ItemSet(1.5f, true);
    public static final ItemSet ORICHALCUM = new ItemSet(1.5f, true);
    public static final ItemSet OSMIUM = new ItemSet(0.8f);
    public static final ItemSet PALLADIUM = new ItemSet(false, true, 1.5f, Item.Properties::fireResistant);
    public static final ItemSet PLATINUM = new ItemSet(0.7f);
    public static final ItemSet PROMETHEUM = new ItemSet(0.7f);
    public static final ItemSet QUADRILLUM = new ItemSet(0.7f);
    public static final ItemSet RUNITE = new ItemSet(1.0f, true);
    public static final ItemSet SILVER = new ItemSet(0.7f);
    public static final ItemSet STAR_PLATINUM = new ItemSet(true);
    public static final ItemSet STEEL = new ItemSet(true);
    public static final ItemSet STORMYX = new ItemSet(1.0f, true);
    public static final ItemSet TIN = new ItemSet(0.2f);

    @Override
    public void processField(ItemSet value, String name, Field field) {
        value.register(name, value.equals(STAR_PLATINUM));
    }

    @Override
    public Class<ItemSet> getTargetFieldType() {
        return ItemSet.class;
    }

    public static class Mats implements ItemRegistryContainer {
        public static final Item AQUARIUM_PEARL = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item BANGLUM_CHUNK = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item CARMOT_STONE = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item PROMETHEUM_BOUQUET = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item DURASTEEL_ENGINE = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item MORKITE = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP));
        public static final Item STARRITE = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item STORMYX_SHELL = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON));
        public static final Item UNOBTAINIUM = new Item(new Item.Properties().group(MythicMetals.TABBED_GROUP).rarity(Rarity.UNCOMMON).fireResistant());
    }

    public static class ParticleSticks implements ItemRegistryContainer {
        public static final Item COMBUSTION_STICK = new ParticleStick<>(new Item.Properties(), MythicParticleSystem.COMBUSTION_EXPLOSION);
        public static final Item SPARK_STICK = new ParticleStick<>(new Item.Properties(), MythicParticleSystem.COPPER_SPARK);
        public static final Item HEART_STICK = new ParticleStick<>(new Item.Properties(), MythicParticleSystem.HEALING_HEARTS);
    }

    public static class Copper implements SimpleFieldProcessingSubject<CopperSet> {
        public static final CopperSet COPPER = new CopperSet();

        @Override
        public void processField(CopperSet value, String name, Field field) {
            value.register(name);
        }

        @Override
        public Class<CopperSet> getTargetFieldType() {
            return CopperSet.class;
        }
    }

    public static class Templates implements ItemRegistryContainer {
        public static final List<ResourceLocation> UNOBTAINIUM_ALLOY_ITEMS = Util.make(new ArrayList<>(SmithingTemplateItem.createNetheriteUpgradeIconList()),
            identifiers -> identifiers.add(RegistryHelper.id("item/template/empty_slot_elytra")));

        public static final List<ResourceLocation> ARMOR_ITEMS = Util.make(new ArrayList<>(),
            identifiers -> {
                identifiers.add(ResourceLocation.parse("item/empty_armor_slot_helmet"));
                identifiers.add(ResourceLocation.parse("item/empty_armor_slot_chestplate"));
                identifiers.add(ResourceLocation.parse("item/empty_armor_slot_leggings"));
                identifiers.add(ResourceLocation.parse("item/empty_armor_slot_boots"));
            });

        public static final Item UNOBTAINIUM_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.unobtainium.applies_to").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.unobtainium.ingredients").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.unobtainium.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.unobtainium.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.unobtainium.additions_slot_description"),
            UNOBTAINIUM_ALLOY_ITEMS,
            SmithingTemplateItem.createNetheriteUpgradeMaterialList()
        );

        public static final Item MYTHRIL_DRILL_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.mythril_drill.applies_to").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.mythril_drill.ingredients").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.mythril_drill.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.mythril_drill.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.mythril_drill.additions_slot_description"),
            List.of(RegistryHelper.id("item/template/empty_slot_mythril_pick")),
            List.of(RegistryHelper.id("item/template/empty_slot_engine"))
        );

        public static final Item MIDAS_FOLDING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.midas_folding.applies_to").withStyle(ChatFormatting.GOLD),
            Component.translatable("smithing_template.mythicmetals.midas_folding.ingredients").withStyle(ChatFormatting.GOLD),
            Component.translatable("smithing_template.mythicmetals.midas_folding.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.midas_folding.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.midas_folding.additions_slot_description"),
            List.of(
                RegistryHelper.id("item/template/empty_slot_midas_dagger"),
                RegistryHelper.id("item/template/empty_slot_midas"),
                RegistryHelper.id("item/template/empty_slot_gilded_midas"),
                RegistryHelper.id("item/template/empty_slot_royal_midas")
            ),
            List.of(RegistryHelper.id("item/template/empty_slot_block"))
        );

        public static final Item ROYAL_MIDAS_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.royal_midas.applies_to").withStyle(ChatFormatting.GOLD),
            Component.translatable("smithing_template.mythicmetals.royal_midas.ingredients").withStyle(ChatFormatting.GOLD),
            Component.translatable("smithing_template.mythicmetals.royal_midas.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.royal_midas.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.royal_midas.additions_slot_description"),
            List.of(RegistryHelper.id("item/template/empty_slot_gilded_midas")),
            List.of(RegistryHelper.id("item/template/empty_slot_block"))
        );

        public static final Item AEGIS_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.aegis.applies_to").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.aegis.ingredients").withStyle(ChatFormatting.BLUE),
            Component.translatable("smithing_template.mythicmetals.aegis.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.aegis.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.aegis.additions_slot_description"),
            List.of(RegistryHelper.id("item/template/empty_slot_hallowed_sword"), RegistryHelper.id("item/template/empty_slot_palladium_sword")),
            SmithingTemplateItem.createNetheriteUpgradeMaterialList()
        );

        public static final Item CARMOT_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.carmot.applies_to").setStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.KYBER.rgb())),
            Component.translatable("smithing_template.mythicmetals.carmot.ingredients").setStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.CARMOT.rgb())),
            Component.translatable("smithing_template.mythicmetals.carmot.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.carmot.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.carmot.additions_slot_description"),
            SmithingTemplateItem.createNetheriteUpgradeIconList(),
            SmithingTemplateItem.createNetheriteUpgradeMaterialList()
        );

        public static final Item OSMIUM_CHAINMAIL_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.osmium.applies_to").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)),
            Component.translatable("smithing_template.mythicmetals.osmium.ingredients").setStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.OSMIUM.rgb())),
            Component.translatable("smithing_template.mythicmetals.osmium.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.osmium.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.osmium.additions_slot_description"),
            ARMOR_ITEMS,
            SmithingTemplateItem.createNetheriteUpgradeMaterialList()
        );

        public static final Item TIDESINGER_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.tidesinger.applies_to").setStyle(UsefulSingletonForColorUtil.MetalColors.AQUA_STYLE),
            Component.translatable("smithing_template.mythicmetals.tidesinger.ingredients").setStyle(UsefulSingletonForColorUtil.MetalColors.BUBBLE.style()),
            Component.translatable("smithing_template.mythicmetals.tidesinger.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.tidesinger.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.tidesinger.additions_slot_description"),
            SmithingTemplateItem.createNetheriteUpgradeIconList(),
            List.of(
                RegistryHelper.id("item/template/empty_slot_brain"),
                RegistryHelper.id("item/template/empty_slot_bubble"),
                RegistryHelper.id("item/template/empty_slot_fire"),
                RegistryHelper.id("item/template/empty_slot_horn"),
                RegistryHelper.id("item/template/empty_slot_tube")
            )
        );

        public static final Item LEGENDARY_BANGLUM_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable("smithing_template.mythicmetals.legendary_banglum.applies_to").setStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.BANGLUM.rgb())),
            Component.translatable("smithing_template.mythicmetals.legendary_banglum.ingredients").setStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.BANGLUM.rgb())),
            Component.translatable("smithing_template.mythicmetals.legendary_banglum.title").withStyle(ChatFormatting.GRAY),
            Component.translatable("smithing_template.mythicmetals.legendary_banglum.base_slot_description"),
            Component.translatable("smithing_template.mythicmetals.legendary_banglum.additions_slot_description"),
            SmithingTemplateItem.createNetheriteUpgradeIconList(),
            List.of(RegistryHelper.id("item/template/empty_slot_chunk"))
        );
    }

}
