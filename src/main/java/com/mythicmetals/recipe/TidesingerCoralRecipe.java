package com.mythicmetals.recipe;

import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.TidesingerPatternComponent;
import com.mythicmetals.data.MythicTags;
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

public record TidesingerCoralRecipe(Ingredient base, Ingredient addition, Ingredient template,
                                    ItemStack result) implements SmithingRecipe {

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
        return this.addition.test(stack) && stack.is(MythicTags.TIDESINGER_CORAL);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level world) {
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider lookup) {
        var armorStack = input.base().transmuteCopy(this.result().getItem(), 1);
        armorStack.set(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.fromStack(input.addition()));
        return armorStack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MythicRecipeSerializers.TIDESINGER_CORAL_RECIPE;
    }

    public static class Serializer extends EndecRecipeSerializer<TidesingerCoralRecipe> {

        public static final StructEndec<TidesingerCoralRecipe> ENDEC = StructEndecBuilder.of(
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("base", recipe -> recipe.base),
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("addition", recipe -> recipe.addition),
            CodecUtils.toEndec(Ingredient.CODEC).fieldOf("template", recipe -> recipe.template),
            MinecraftEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            TidesingerCoralRecipe::new
        );

        public Serializer(StructEndec<TidesingerCoralRecipe> endec) {
            super(endec);
        }
    }
}
