package com.mythicmetals.mixin;

import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.PrometheumComponent;
import com.mythicmetals.data.MythicTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mythicmetals.component.PrometheumComponent.createOvergrownModifier;
import static com.mythicmetals.component.PrometheumComponent.createOvergrownToughnessModifier;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "verifyComponentsAfterLoad", at = @At("HEAD"))
    private void mythicmetals$dynamicAttributeHandler(ItemStack stack, CallbackInfo ci) {
        if (!stack.is(MythicTags.AUTO_REPAIR)) return;
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) return;
        var prometheumComponent = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);

        // Handle Overgrown modifiers
        // Armor gets armor and toughness. Anything else gets extra damage
        if (prometheumComponent.isOvergrown()) {
            if (stack.getItem() instanceof ArmorItem item) {
                var attributeComponent = item.getDefaultAttributeModifiers();
                var changedComponent = attributeComponent
                    .withModifierAdded(Attributes.ARMOR, createOvergrownModifier(stack, 1, item.getEquipmentSlot()), EquipmentSlotGroup.bySlot(item.getEquipmentSlot()))
                    .withModifierAdded(Attributes.ARMOR_TOUGHNESS, createOvergrownToughnessModifier(stack, 0), EquipmentSlotGroup.bySlot(item.getEquipmentSlot()));
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, changedComponent);
            }
            else if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
                var attributeComponent = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
                var modifier = createOvergrownModifier(stack, 0);
                var changedComponent = attributeComponent.withModifierAdded(Attributes.ATTACK_DAMAGE, modifier, EquipmentSlotGroup.MAINHAND);
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, changedComponent);
            }
        }
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void mythicmetals$inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClientSide()) return;

        if (stack.has(MythicDataComponents.PROMETHEUM)) {
            PrometheumComponent.tickAutoRepair(stack, world);
        }
    }
}
