package com.mythicmetals.conditions;

import com.mojang.serialization.MapCodec;
import com.mythicmetals.MythicMetals;
import net.neoforged.neoforge.common.conditions.ICondition;

public class DustLoadedCondition implements ICondition {
    public static final MapCodec<DustLoadedCondition> CODEC = MapCodec.unit(DustLoadedCondition::new);

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(IContext context) {
        return MythicMetals.CONFIG.enableDusts();
    }
}
