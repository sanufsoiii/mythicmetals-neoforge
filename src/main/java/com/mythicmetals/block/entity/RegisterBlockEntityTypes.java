package com.mythicmetals.block.entity;

import com.mythicmetals.block.MythicBlocks;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import java.lang.reflect.Field;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RegisterBlockEntityTypes implements BlockEntityRegistryContainer {

    public static final BlockEntityType<AquariumResonatorBlockEntity> AQUARIUM_RESONATOR =
            BlockEntityType.Builder.of(AquariumResonatorBlockEntity::new, MythicBlocks.AQUARIUM_RESONATOR).build(null);

    public static final BlockEntityType<EnchantedMidasGoldBlockEntity> ENCHANTED_MIDAS_GOLD_BLOCK =
        BlockEntityType.Builder.of(EnchantedMidasGoldBlockEntity::new, MythicBlocks.ENCHANTED_MIDAS_GOLD_BLOCK).build(null);

    public static final BlockEntityType<CarmotBellBlockEntity> CARMOT_BELL_BLOCK =
        BlockEntityType.Builder.of(CarmotBellBlockEntity::new, MythicBlocks.CARMOT_BELL_BLOCK).build(null);

    @Override
    public boolean shouldProcessField(BlockEntityType<?> value, String identifier, Field field) {
        return BlockEntityRegistryContainer.super.shouldProcessField(value, identifier, field);
    }
}
