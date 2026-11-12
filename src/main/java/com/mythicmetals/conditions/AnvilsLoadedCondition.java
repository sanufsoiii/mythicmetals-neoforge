package com.mythicmetals.conditions;

import com.mojang.serialization.MapCodec;
import com.mythicmetals.MythicMetals;
import net.neoforged.neoforge.common.conditions.ICondition;

public class AnvilsLoadedCondition implements ICondition {
    public static final MapCodec<AnvilsLoadedCondition> CODEC = MapCodec.unit(AnvilsLoadedCondition::new);

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(IContext context) {
        return MythicMetals.CONFIG.enableAnvils();
    }
}
