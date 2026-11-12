package com.mythicmetals.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@Mixin(AbstractMinecart.Type.class)
public abstract class AbstractMinecartEntityTypeMixin {
    @Shadow
    @Final
    @Mutable
    private static AbstractMinecart.Type[] $VALUES;

    @Unique
    private static final AbstractMinecart.Type BANGLUM_TNT = mythicmetals$addType("BANGLUM_TNT");

    @Unique
    private static final AbstractMinecart.Type PALLADIUM_MINECART = mythicmetals$addType("PALLADIUM_MINECART");

    @Invoker("<init>")
    public static AbstractMinecart.Type mythicmetals$init(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Unique
    private static AbstractMinecart.Type mythicmetals$addType(String internalName) {
        ArrayList<AbstractMinecart.Type> types = new ArrayList<>(Arrays.asList(AbstractMinecartEntityTypeMixin.$VALUES));
        AbstractMinecart.Type type = mythicmetals$init(internalName, types.get(types.size() - 1).ordinal() + 1);
        types.add(type);
        AbstractMinecartEntityTypeMixin.$VALUES = types.toArray(new AbstractMinecart.Type[0]);
        return type;
    }
}
