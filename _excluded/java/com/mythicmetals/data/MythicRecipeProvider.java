package com.mythicmetals.data;

import com.mythicmetals.armor.ArmorSet;
import com.mythicmetals.armor.MythicArmor;
import com.mythicmetals.block.BlockSet;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.conditions.DustLoadedCondition;
import com.mythicmetals.conditions.NuggetsLoadedCondition;
import com.mythicmetals.item.ItemSet;
import com.mythicmetals.item.MythicItems;
import com.mythicmetals.misc.RegistryHelper;
import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MythicRecipeProvider extends FabricRecipeProvider {

    public MythicRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void buildRecipes(RecipeOutput exporter) {
        var itemSets = new HashMap<String, ItemSet>();
        var blockSets = new HashMap<String, BlockSet>();

        var dustExporter = withConditions(exporter, new DustLoadedCondition());
        var nuggetExporter = withConditions(exporter, new NuggetsLoadedCondition());

        // Handle items first, as they store whether the items need blasting to be smelted
        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.class, ItemSet.class, (itemSet, name, field) -> {
            itemSets.put(name, itemSet);
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicBlocks.class, BlockSet.class, (blockSet, name, field) -> {
            blockSets.put(name, blockSet);
        });

        // Smelting armor into nuggets
        ReflectionUtils.iterateAccessibleStaticFields(MythicArmor.class, ArmorSet.class, (armorSet, name, field) -> {
            if (itemSets.containsKey(name) && itemSets.get(name).getNugget() != null) {
                var itemSet = itemSets.get(name);
                boolean requiresBlasting = itemSet.requiresBlasting();
                var nugget = itemSet.getNugget();
                ItemLike[] armorItems = new ItemLike[0];
                armorItems = armorSet.getArmorItems().toArray(armorItems);

                if (!requiresBlasting) {
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(armorItems), RecipeCategory.MISC, nugget, 0.1f, 200)
                        .unlockedBy("has_material", has(TagKey.create(Registries.ITEM, RegistryHelper.id("nuggets/" + name))))
                        .save(nuggetExporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_nugget_from_armor"));
                }
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(armorItems), RecipeCategory.MISC, nugget, 0.1f, 100)
                    .unlockedBy("has_material", has(TagKey.create(Registries.ITEM, RegistryHelper.id("nuggets/" + name))))
                    .save(nuggetExporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_nugget_from_armor"));
            }
        });

        // Smelting ore blocks into ingots
        itemSets.forEach((name, itemSet) -> {
            // Smelting Ore Blocks into ingots
            if (blockSets.containsKey(name) && blockSets.get(name).getOre() != null) {
                var blockSet = blockSets.get(name);
                var oreList = new ArrayList<>(blockSet.getOreVariants());
                oreList.add(blockSet.getOre());
                var items = oreList.stream().map(Block::asItem).toList().toArray(new Item[0]);

                var ingot = itemSet.getIngot();
                var xp = itemSet.getXp();
                boolean requiresBlasting = itemSet.requiresBlasting();
                var critera = inventoryTrigger(ItemPredicate.Builder.item().of(items).build());

                if (!requiresBlasting) {
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(items), RecipeCategory.MISC, ingot, xp, 200)
                        .unlockedBy("has_material", critera)
                        .save(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_ores"));
                }
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(items), RecipeCategory.MISC, ingot, xp, 100)
                    .unlockedBy("has_material", critera)
                    .save(exporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_ores"));
            }

            // Smelting Raw Ores into ingots
            if (itemSet.getRawOre() != null) {
                if (!itemSet.requiresBlasting()) {
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemSet.getRawOre()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 200)
                        .unlockedBy("has_material", has(itemSet.getRawOre()))
                        .save(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_raw_ore"));
                }
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemSet.getRawOre()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 100)
                    .unlockedBy("has_material", has(itemSet.getRawOre()))
                    .save(exporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_raw_ore"));
            }
            // Smelting dusts into ingots
            if (itemSet.getDust() != null) {
                if (!itemSet.requiresBlasting()) {
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemSet.getDust()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 200)
                        .unlockedBy("has_material", has(TagKey.create(Registries.ITEM, RegistryHelper.id("dusts/" + name))))
                        .save(dustExporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_dust"));
                }
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemSet.getDust()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 100)
                    .unlockedBy("has_material", has(TagKey.create(Registries.ITEM, RegistryHelper.id("dusts/" + name))))
                    .save(dustExporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_dust"));
            }
        });


    }
}
