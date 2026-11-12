package com.mythicmetals.registry;

import com.google.common.collect.ImmutableSet;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class RegisterPointOfInterests {
    // Fabric PointOfInterestHelper.register(id, ticketCount 0, searchDistance 1, block): all states of the block.
    // NeoForge's PoiType registry callback fills the blockstate -> POI map.
    public static final PoiType CONDUIT_POWERED_BLOCK = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE,
        RegistryHelper.id("conduit_powered_block"),
        new PoiType(ImmutableSet.copyOf(MythicBlocks.AQUARIUM_RESONATOR.getStateDefinition().getPossibleStates()), 0, 1)
    );

    public static void init() {
    }
}
