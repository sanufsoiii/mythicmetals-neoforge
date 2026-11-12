package com.mythicmetals.item.tools;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;

/**
 * Used to handle Auto Repair behavior regarding more gracefully
 * <p>
 * NeoForge: Fabric's {@code FabricItem} hooks map to {@link IItemExtension}. Fabric only consults its hooks when the
 * item stayed the same but its components changed, which is reproduced here before applying the original rules.
 */
public interface AutoRepairable extends IItemExtension {

    // Don't interrupt mining if durability is repaired
    // Might affect mending as a side effect
    // (Fabric: allowContinuingBlockBreaking -> oldStack.getDamageValue() != newStack.getDamageValue())
    @Override
    default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        if (!newStack.is(oldStack.getItem())) return true;
        if (ItemStack.isSameItemSameComponents(oldStack, newStack)) return false;
        boolean allowContinuingBlockBreaking = oldStack.getDamageValue() != newStack.getDamageValue();
        return !allowContinuingBlockBreaking;
    }

    // Don't update the item in hand if durability is repaired
    // Might affect mending as a side effect
    // (Fabric: allowComponentsUpdateAnimation -> oldStack.getDamageValue() == newStack.getDamageValue())
    @Override
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged && newStack.is(oldStack.getItem()) && !ItemStack.isSameItemSameComponents(oldStack, newStack)) {
            return oldStack.getDamageValue() == newStack.getDamageValue();
        }
        return IItemExtension.super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}
