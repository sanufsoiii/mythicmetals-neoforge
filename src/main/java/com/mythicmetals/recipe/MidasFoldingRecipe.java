package com.mythicmetals.recipe;

import com.mythicmetals.component.GoldFoldedComponent;
import com.mythicmetals.item.MythicItems;
import com.mythicmetals.item.tools.MythicTools;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.EndecRecipeSerializer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import static com.mythicmetals.component.MythicDataComponents.GOLD_FOLDED;

public record MidasFoldingRecipe(Ingredient template, Ingredient base, Ingredient addition,
                                 ItemStack result) implements SmithingRecipe {

    @Override
    public boolean matches(SmithingRecipeInput input, Level world) {
        // Regular test
        if (!(this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition()))) {
            return false;
        }
        var stack = input.base();

        if (!stack.has(GOLD_FOLDED)) return false;
        int goldCount = stack.has(GOLD_FOLDED) ? stack.get(GOLD_FOLDED).goldFolded() : 0;

        if (input.template().getItem().equals(MythicItems.Templates.ROYAL_MIDAS_SMITHING_TEMPLATE)) {
            return goldCount >= 640;
        }

        if (stack.getItem().equals(MythicTools.ROYAL_MIDAS_GOLD_SWORD)) {
            return goldCount >= 640 && goldCount < 10000;
        }

        return goldCount < 640;
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, HolderLookup.Provider lookup) {
        var swordInputStack = input.base().copy();

        var goldComponent = swordInputStack.getOrDefault(GOLD_FOLDED, GoldFoldedComponent.of(0));
        int goldCount = goldComponent.goldFolded();
        swordInputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1, goldComponent.isRoyal()));

        // Gilded Midas Gold Sword handler
        if (swordInputStack.getItem().equals(MythicTools.GILDED_MIDAS_GOLD_SWORD)) {

            // Transform into Royal Midas Gold Sword
            if (goldCount >= 640) {
                var swordnite = swordInputStack.transmuteCopy(MythicTools.ROYAL_MIDAS_GOLD_SWORD, 1);
                swordnite.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1, true));
                return swordnite;
            }
        }

        // Handle Midas Gold Sword, transform if you fold and it at least has 320 gold on it
        if (swordInputStack.getItem().equals(MythicTools.MIDAS_GOLD_SWORD)) {

            // Transform Midas Gold Sword into Gilded Midas Gold Sword
            if (goldCount >= 319) {
                var swordnite = swordInputStack.transmuteCopy(MythicTools.GILDED_MIDAS_GOLD_SWORD, 1);
                swordnite.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));
                return swordnite;
            }
        }

        return swordInputStack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return this.result;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return MythicRecipeSerializers.MIDAS_FOLDING_RECIPE;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    public static class Serializer extends EndecRecipeSerializer<MidasFoldingRecipe> {
        private static final StructEndec<MidasFoldingRecipe> ENDEC = StructEndecBuilder.of(
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("template", recipe -> recipe.template),
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("base", recipe -> recipe.base),
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("addition", recipe -> recipe.addition),
            MinecraftEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            MidasFoldingRecipe::new
        );

        public Serializer() {
            super(ENDEC);
        }
    }
}
