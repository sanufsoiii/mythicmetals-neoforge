package com.mythicmetals.compat;

import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.TidesingerPatternComponent;
import com.mythicmetals.data.MythicTags;
import com.mythicmetals.recipe.TidesingerCoralRecipe;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class TidesingerEMIRecipe implements EmiRecipe {

    Ingredient template;
    Ingredient base;
    Ingredient addition;
    List<EmiIngredient> inputs;
    EmiStack outputs;
    ResourceLocation id;

    public TidesingerEMIRecipe(TidesingerCoralRecipe recipe, ResourceLocation id) {
        this.id = id;
        this.template = recipe.template();
        this.base = recipe.base();
        this.addition = recipe.addition();

        if (this.base != null && this.addition != null && template != null) {
            var inputStack = Arrays.stream(this.base.getItems()).findFirst().orElse(ItemStack.EMPTY).copy();
            inputs = List.of(
                EmiIngredient.of(this.template),
                EmiStack.of(inputStack),
                EmiIngredient.of(this.addition)
            );

            var additionStack = Arrays.stream(this.addition.getItems()).findFirst().orElse(ItemStack.EMPTY).copy();
            var outputStack = recipe.result();
            if (outputStack != null && additionStack.is(MythicTags.TIDESINGER_CORAL)) {
                var outputWithComponents = outputStack.transmuteCopy(outputStack.getItem(), outputStack.getCount());
                outputWithComponents.set(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.fromStack(additionStack));
                outputs = EmiStack.of(outputWithComponents);
            }
        }


    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.SMITHING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(outputs);
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 18, 0);
        widgets.addSlot(inputs.get(2), 36, 0);
        widgets.addSlot(outputs, 94, 0).recipeContext(this);
    }
}
