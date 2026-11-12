package com.mythicmetals.entity;

import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class MythicEntityAttributes {
    public static final Holder<Attribute> CARMOT_SHIELD = RegistryHelper.entityAttribute("carmot_shield", new RangedAttribute("attribute.name.generic.mythicmetals.carmot_shield", 0, 0, 2048).setSyncable(true));
    public static final Holder<Attribute> ELYTRA_ROCKET_SPEED = RegistryHelper.entityAttribute("elytra_rocket_speed", new RangedAttribute("attribute.name.generic.mythicmetals.elytra_rocket_speed", 1, 0, 1024).setSyncable(true));
    public static final Holder<Attribute> FIRE_VULNERABILITY = RegistryHelper.entityAttribute("fire_vulnerability", new RangedAttribute("attribute.name.generic.mythicmetals.fire_vulnerability", 0, 0, 2048).setSyncable(true));

    public static void init() {
    }
}
