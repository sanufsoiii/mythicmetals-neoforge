package com.mythicmetals.item.tools;

import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE;

public class AquariumToolSet extends ToolSet {
    public static final String ABILITY_MODIFIER = "aquarium_tool_underwater_mining_speed_bonus";
    public AquariumToolSet(Tier material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    public ItemAttributeModifiers.Builder createAttributeBuilder(Tier material, double damage, float speed) {
        return super.createAttributeBuilder(material, damage, speed)
            .add(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(RegistryHelper.id(ABILITY_MODIFIER), 1.0f, ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }
}
