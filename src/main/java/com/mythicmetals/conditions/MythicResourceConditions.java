package com.mythicmetals.conditions;

import com.mojang.serialization.MapCodec;
import com.mythicmetals.MythicMetals;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Simple resource conditions that check if the corresponding {@link MythicMetals#CONFIG} booleans are enabled.
 * <br>
 * This is used to check if anvils or nuggets are disabled, so that errors regarding them can be surpressed.
 * <br>
 * NeoForge: registered as condition codecs under the same ids the data files use.
 */
public class MythicResourceConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MythicMetals.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<AnvilsLoadedCondition>> ANVILS = CONDITION_CODECS.register("anvils_enabled", () -> AnvilsLoadedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<DustLoadedCondition>> DUSTS = CONDITION_CODECS.register("dust_enabled", () -> DustLoadedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<NuggetsLoadedCondition>> NUGGETS = CONDITION_CODECS.register("nuggets_enabled", () -> NuggetsLoadedCondition.CODEC);

    public static void init(IEventBus modBus) {
        CONDITION_CODECS.register(modBus);
    }
}
