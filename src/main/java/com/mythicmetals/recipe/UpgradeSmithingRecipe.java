package com.mythicmetals.recipe;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.UpgradeComponent;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.EndecRecipeSerializer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public record UpgradeSmithingRecipe(Ingredient base, Ingredient addition, ItemStack result) implements SmithingRecipe {

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level world) {
        boolean validRecipe = this.base.test(input.base()) && this.addition.test(input.addition());
        var addition = input.addition().getItem();
        var upgrades = input.base().get(MythicDataComponents.UPGRADES);
        if (upgrades == null) return false;

        boolean isUpgradeValid = !addition.equals(Items.AIR) && !upgrades.hasUpgrade(addition);

        return validRecipe && isUpgradeValid && upgrades.hasFreeSlots();
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, HolderLookup.Provider lookup) {
        var stack = input.base().copy();

        // Apply drill upgrade
        stack.set(MythicDataComponents.UPGRADES, UpgradeComponent.addItem(stack.get(MythicDataComponents.UPGRADES), input.addition().getItem()));
        return stack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MythicRecipeSerializers.UPGRADE_SMITHING_RECIPE_SERIALIZER;
    }

    public static class Serializer extends EndecRecipeSerializer<UpgradeSmithingRecipe> {

        public static final StructEndec<UpgradeSmithingRecipe> ENDEC = StructEndecBuilder.of(
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("base", recipe -> recipe.base),
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("addition", recipe -> recipe.addition),
            MinecraftEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            UpgradeSmithingRecipe::new
        );

        public Serializer(StructEndec<UpgradeSmithingRecipe> endec) {
            super(endec);
        }
    }
}
