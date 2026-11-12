package com.mythicmetals.data;

import com.mythicmetals.armor.ArmorSet;
import com.mythicmetals.armor.MythicArmor;
import com.mythicmetals.block.BlockSet;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.item.ItemSet;
import com.mythicmetals.item.MythicItems;
import com.mythicmetals.item.tools.MythicTools;
import com.mythicmetals.item.tools.ToolSet;
import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import java.util.concurrent.CompletableFuture;

public class MythicItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public MythicItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void addTags(HolderLookup.Provider arg) {
        ReflectionUtils.iterateAccessibleStaticFields(MythicBlocks.class, BlockSet.class, (blockSet, name, field) -> {
            if (blockSet.getOre() != null) {
                var string = "ores/" + name;
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = ConventionalItemTags.ORES;
                var tagBuilder = tag(modTag).add(blockSet.getOre().asItem());
                tag(commonTag).addTag(modTag);

                if (!blockSet.getOreVariants().isEmpty()) {
                    blockSet.getOreVariants().forEach(block -> tagBuilder.add(block.asItem()));
                }
            }

            if (blockSet.getStorageBlock() != null) {
                var string = "storage_blocks/" + name;
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                var commonBlocksTag = ConventionalItemTags.STORAGE_BLOCKS;
                tag(modTag).add(blockSet.getStorageBlock().asItem());
                tag(commonTag).add(blockSet.getStorageBlock().asItem());
                tag(commonBlocksTag).addTag(modTag);
                if (blockSet.getOreStorageBlock() != null) {
                    string = "storage_blocks/raw_" + name;
                    modTag = MythicMetalsData.createModItemTag(string);
                    tag(modTag)
                        .add(blockSet.getOreStorageBlock().asItem());
                    tag(commonBlocksTag)
                        .addTag(modTag);
                }
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.class, ItemSet.class, (itemSet, name, field) -> {
            /*
             * Create ingot tags. Example:
             * Adamantite Ingot is added to the following:
             * #mythicmetals:adamantite_ingots
             * #c:adamantite_ingots
             * #mythicmetals:ingots
             * At the end #mythicmetals:ingots is nested into #c:ingots
             */
            var modIngotTag = MythicMetalsData.createModItemTag(ConventionalItemTags.INGOTS.location().getPath());
            var commonIngotTag = ConventionalItemTags.INGOTS;
            if (itemSet.getIngot() != null) {
                // Star Platinum is explicitly named, so this is for handling that edge case
                var string = itemSet.equals(MythicItems.STAR_PLATINUM) ? name : ConventionalItemTags.INGOTS.location().getPath() + "/" + name;
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                tag(modTag).add(itemSet.getIngot());
                tag(commonTag).addTag(modTag);
                tag(modIngotTag).add(itemSet.getIngot());
            }
            tag(commonIngotTag).addTag(modIngotTag);

            /*
             * Create raw ore tags. Example:
             * Raw Adamantite is added to the following:
             * - #mythicmetals:raw_materials/adamantite
             * - #c:raw_materials/adamantite
             * - #mythicmetals:raw_materials
             */
            if (itemSet.getRawOre() != null) {
                var string = "raw_materials/" + name;
                var modRawOreTag = MythicMetalsData.createModItemTag(ConventionalItemTags.RAW_MATERIALS.location().getPath());

                // Edge case: Midas Gold can combine with any raw ore to make gold, except itself
                var midasRawOreTag = MythicMetalsData.createModItemTag("midas_raw_ores");
                if (!itemSet.equals(MythicItems.MIDAS_GOLD)) {
                    tag(midasRawOreTag).add(itemSet.getRawOre());
                }
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = ConventionalItemTags.RAW_MATERIALS;
                tag(modTag)
                    .add(itemSet.getRawOre());
                tag(modRawOreTag)
                    .add(itemSet.getRawOre());
                tag(commonTag)
                    .addTag(modTag);
            }

            /*
             * Create nugget tags. Example:
             * Adamantite Nugget is added to the following:
             * #mythicmetals:nuggets/adamantite
             * #c:nuggets/adamantite
             * #mythicmetals:nuggets
             */
            if (itemSet.getNugget() != null) {
                var string = "nuggets/" + name;
                var modRawOreTag = MythicMetalsData.createModItemTag("nuggets");

                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = ConventionalItemTags.NUGGETS;
                tag(modTag)
                    .addOptional(BuiltInRegistries.ITEM.getKey(itemSet.getNugget()));
                tag(modRawOreTag)
                    .addOptional(BuiltInRegistries.ITEM.getKey(itemSet.getNugget()));
                tag(commonTag)
                    .addOptionalTag(modTag);
            }

            /*
             * Create nugget tags. Example:
             * Adamantite Nugget is added to the following:
             * #mythicmetals:nuggets/adamantite
             * #c:nuggets/adamantite
             * #mythicmetals:nuggets
             */
            if (itemSet.getDust() != null) {
                var string = "dusts/" + name;
                var modRawOreTag = MythicMetalsData.createModItemTag("dusts");

                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = ConventionalItemTags.DUSTS;
                tag(modTag)
                    .addOptional(BuiltInRegistries.ITEM.getKey(itemSet.getDust()));
                tag(modRawOreTag)
                    .addOptionalTag(BuiltInRegistries.ITEM.getKey(itemSet.getDust()));
                tag(commonTag)
                    .addOptionalTag(modTag);
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.Mats.class, Item.class, (item, name, field) -> {
            if (item.equals(MythicItems.Mats.STARRITE) || item.equals(MythicItems.Mats.UNOBTAINIUM)) {
                var modTag = MythicMetalsData.createModItemTag(name);
                var commonTag = MythicMetalsData.createCommonItemTag(name);
                tag(modTag).add(item);
                tag(commonTag).addTag(modTag);
            } else {
                var rareMaterials = MythicMetalsData.createModItemTag("rare_materials");
                tag(rareMaterials).add(item);
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicTools.class, ToolSet.class, (toolSet, name, field) -> {
            var toolModTag = MythicMetalsData.createModItemTag("tools/" + name);
            var equipmentModTag = MythicMetalsData.createModItemTag("equipment/" + name);
            var toolsModTag = MythicMetalsData.createModItemTag("tools");
            var commonTag = ConventionalItemTags.TOOLS;
            var commonEquipmentTag = MythicMetalsData.createCommonItemTag("equipment");

            // Add to tool tags
            var toolArray = toolSet.get().toArray(new Item[0]);
            tag(toolModTag)
                .add(toolArray);
            tag(toolsModTag)
                .add(toolArray);
            tag(equipmentModTag)
                .add(toolArray);
            tag(commonTag)
                .addTag(toolModTag);
            tag(commonEquipmentTag)
                .addTag(equipmentModTag);

            // Melee weapons
            tag(MythicMetalsData.createModItemTag(ConventionalItemTags.MELEE_WEAPON_TOOLS.location().getPath()))
                .add(toolSet.getSword())
                .add(toolSet.getAxe());
            tag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
                .add(toolSet.getSword())
                .add(toolSet.getAxe());

            // Swords
            tag(MythicMetalsData.createModItemTag("swords"))
                .add(toolSet.getSword());
            tag(ItemTags.SWORDS)
                .add(toolSet.getSword());

            // Mining tools
            tag(MythicMetalsData.createModItemTag(ConventionalItemTags.MINING_TOOL_TOOLS.location().getPath()))
                .add(toolSet.getPickaxe());
            tag(ConventionalItemTags.MINING_TOOL_TOOLS)
                .add(toolSet.getPickaxe());

            // Pickaxes
            tag(MythicMetalsData.createModItemTag("pickaxes"))
                .add(toolSet.getPickaxe());
            tag(ItemTags.PICKAXES)
                .add(toolSet.getPickaxe());

            // Axes
            tag(MythicMetalsData.createModItemTag("axes"))
                .add(toolSet.getAxe());
            tag(ItemTags.AXES)
                .add(toolSet.getAxe());

            // Shovels
            tag(MythicMetalsData.createModItemTag("shovels"))
                .add(toolSet.getShovel());
            tag(ItemTags.SHOVELS)
                .add(toolSet.getShovel());

            // Hoes
            tag(MythicMetalsData.createModItemTag("hoes"))
                .add(toolSet.getHoe());
            tag(ItemTags.HOES)
                .add(toolSet.getHoe());


        });

        // Edge cases from Mythic Tools
        // Swords
        tag(MythicMetalsData.createModItemTag(ConventionalItemTags.MELEE_WEAPON_TOOLS.location().getPath()))
            .add(MythicTools.RED_AEGIS_SWORD)
            .add(MythicTools.WHITE_AEGIS_SWORD)
            .add(MythicTools.MIDAS_GOLD_SWORD)
            .add(MythicTools.GILDED_MIDAS_GOLD_SWORD)
            .add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        tag(MythicMetalsData.createModItemTag("swords"))
            .add(MythicTools.RED_AEGIS_SWORD)
            .add(MythicTools.WHITE_AEGIS_SWORD)
            .add(MythicTools.MIDAS_GOLD_SWORD)
            .add(MythicTools.GILDED_MIDAS_GOLD_SWORD)
            .add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        tag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
            .add(MythicTools.RED_AEGIS_SWORD)
            .add(MythicTools.WHITE_AEGIS_SWORD)
            .add(MythicTools.MIDAS_GOLD_SWORD)
            .add(MythicTools.GILDED_MIDAS_GOLD_SWORD)
            .add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        tag(ItemTags.SWORD_ENCHANTABLE)
            .add(MythicTools.RED_AEGIS_SWORD)
            .add(MythicTools.WHITE_AEGIS_SWORD)
            .add(MythicTools.MIDAS_GOLD_SWORD)
            .add(MythicTools.GILDED_MIDAS_GOLD_SWORD)
            .add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        // Mining Tools + Pickaxe Tag
        tag(ItemTags.PICKAXES)
            .add(MythicTools.MYTHRIL_DRILL)
            .add(MythicTools.ORICHALCUM_HAMMER);
        tag(MythicMetalsData.createModItemTag("pickaxes"))
            .add(MythicTools.MYTHRIL_DRILL)
            .add(MythicTools.ORICHALCUM_HAMMER);
        tag(MythicMetalsData.createModItemTag(ConventionalItemTags.MINING_TOOL_TOOLS.location().getPath()))
            .add(MythicTools.MYTHRIL_DRILL)
            .add(MythicTools.ORICHALCUM_HAMMER);
        tag(ConventionalItemTags.MINING_TOOL_TOOLS)
            .add(MythicTools.MYTHRIL_DRILL)
            .add(MythicTools.ORICHALCUM_HAMMER);
        // Arrows
        tag(MythicMetalsData.createModItemTag("arrows"))
            .add(MythicTools.RUNITE_ARROW)
            .add(MythicTools.TIPPED_RUNITE_ARROW)
            .add(MythicTools.STAR_PLATINUM_ARROW);
        tag(MythicMetalsData.createCommonItemTag("arrows"))
            .add(MythicTools.RUNITE_ARROW)
            .add(MythicTools.TIPPED_RUNITE_ARROW)
            .add(MythicTools.STAR_PLATINUM_ARROW);
        // Shields
        tag(MythicMetalsData.createModItemTag(ConventionalItemTags.SHIELD_TOOLS.location().getPath()))
            .add(MythicTools.STORMYX_SHIELD);
        tag(ConventionalItemTags.SHIELD_TOOLS)
            .add(MythicTools.STORMYX_SHIELD);

        ReflectionUtils.iterateAccessibleStaticFields(MythicArmor.class, ArmorSet.class, (armorSet, name, field) -> {
            var modTag = MythicMetalsData.createModItemTag("armor/" + name);
            var modArmorTag = MythicMetalsData.createModItemTag("armor");
            TagKey<Item> modEquipmentTag;
            var commonTag = ConventionalItemTags.ARMORS;
            var commonEquipmentTag = MythicMetalsData.createModItemTag("equipment");
            // Edge case - Osmium Chainmail is Osmium Equipment
            if (armorSet.equals(MythicArmor.OSMIUM_CHAINMAIL)) {
                modEquipmentTag = MythicMetalsData.createModItemTag("equipment/osmium");
                armorSet.getArmorItems().forEach(armorItem -> {
                    tag(modTag).add(armorItem);
                    tag(modEquipmentTag).add(armorItem);
                });
            } else {
                modEquipmentTag = MythicMetalsData.createModItemTag("equipment/" + name);
                armorSet.getArmorItems().forEach(armorItem -> {
                    switch (armorItem.getEquipmentSlot()) {
                        case HEAD -> {
                            tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(armorItem);
                            tag(ItemTags.HEAD_ARMOR).add(armorItem);
                        }
                        case CHEST -> {
                            tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(armorItem);
                            tag(ItemTags.CHEST_ARMOR).add(armorItem);
                        }
                        case LEGS -> {
                            tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(armorItem);
                            tag(ItemTags.LEG_ARMOR).add(armorItem);
                        }
                        case FEET -> {
                            tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(armorItem);
                            tag(ItemTags.FOOT_ARMOR).add(armorItem);
                        }
                        case null, default -> {
                            // no-op
                        }
                    }
                    tag(modTag).add(armorItem);
                    tag(modEquipmentTag).add(armorItem);
                });
            }
            tag(modArmorTag).addTag(modTag);
            tag(commonTag).addTag(modTag);
            tag(commonEquipmentTag).addTag(modEquipmentTag);
        });

        /*
         * Edge cases for Mythic Armor (The Celestium Elytra)
         */
        tag(MythicMetalsData.createModItemTag("equipment/celestium"))
            .add(MythicArmor.CELESTIUM_ELYTRA);
        tag(MythicMetalsData.createModItemTag("armor/celestium"))
            .add(MythicArmor.CELESTIUM_ELYTRA);
        tag(MythicMetalsData.createModItemTag("elytra"))
            .add(MythicArmor.CELESTIUM_ELYTRA);
        tag(MythicMetalsData.createCommonItemTag("elytra"))
            .add(MythicArmor.CELESTIUM_ELYTRA);

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.Templates.class, Item.class, (item, name, field) -> {
            var modTag = MythicMetalsData.createModItemTag("smithing_templates");
            var commonTag = MythicMetalsData.createCommonItemTag("smithing_templates");
            tag(modTag).add(item);
            tag(commonTag).add(item);
        });

    }
}
