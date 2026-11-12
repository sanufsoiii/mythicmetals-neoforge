package com.mythicmetals.armor;

import com.mythicmetals.entity.MythicEntityAttributes;
import com.mythicmetals.item.MythicItems;
import com.mythicmetals.misc.RegistryHelper;
import com.mythicmetals.registry.RegisterSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.gameevent.GameEvent;

public class CelestiumElytra extends ElytraItem {
    public CelestiumElytra(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(MythicItems.CELESTIUM.getIngot());
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(RegisterSounds.EQUIP_CELESTIUM_ELYTRA);
    }

    public static boolean isWearing(LivingEntity entity) {
        var stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        return stack.is(MythicArmor.CELESTIUM_ELYTRA) && isFlyEnabled(stack);
    }

    // Fabric: FabricElytraItem#useCustomElytra -> fly while isFlyEnabled, then doVanillaElytraTick.
    // NeoForge: ElytraItem#canElytraFly already checks isFlyEnabled; the tick is overridden here.
    @Override
    public boolean elytraFlightTick(ItemStack chestStack, LivingEntity entity, int flightTicks) {
        doVanillaElytraTick(entity, chestStack);
        return true;
    }

    public void doVanillaElytraTick(LivingEntity entity, ItemStack chestStack) {
        int nextRoll = entity.getFallFlyingTicks() + 1;

        if (!entity.level().isClientSide && nextRoll % 10 == 0) {
            if ((nextRoll / 10) % 4 == 0) {
                chestStack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
            }

            entity.gameEvent(GameEvent.ELYTRA_GLIDE);
        }
    }

    public static ItemAttributeModifiers createDefaultAttributes() {
        var builder = ItemAttributeModifiers.builder();
        var armor = new AttributeModifier(RegistryHelper.id("celestium_elytra_armor_protection"), 5.0F, AttributeModifier.Operation.ADD_VALUE);
        var toughness = new AttributeModifier(RegistryHelper.id("celestium_elytra_armor_toughness"), 3.0F, AttributeModifier.Operation.ADD_VALUE);
        var speed = new AttributeModifier(RegistryHelper.id("celestium_elytra_speed_bonus"), 0.08F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        var rocketSpeedBonus = new AttributeModifier(RegistryHelper.id("celestium_elytra_rocket_speed_bonus"), 0.20F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        builder.add(Attributes.ARMOR, armor, EquipmentSlotGroup.CHEST);
        builder.add(Attributes.ARMOR_TOUGHNESS, toughness, EquipmentSlotGroup.CHEST);
        builder.add(Attributes.MOVEMENT_SPEED, speed, EquipmentSlotGroup.CHEST);
        builder.add(MythicEntityAttributes.ELYTRA_ROCKET_SPEED, rocketSpeedBonus, EquipmentSlotGroup.CHEST);
        return builder.build();
    }
}
