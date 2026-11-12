package com.mythicmetals.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mythicmetals.MythicMetals;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge replacement for Fabric's {@code BiomeModifications.addFeature(BiomeSelectors.tag(...), step, feature)}.
 * <p>
 * {@link MythicOreFeatures#init()} keeps deciding which ores generate (config flags, biome tags, step) exactly as on
 * Fabric and records them here; the single modifier {@code data/mythicmetals/neoforge/biome_modifier/ores.json}
 * adds the recorded placed features to matching biomes in the ADD phase (Fabric: ADDITIONS).
 */
public record MythicOreBiomeModifier(HolderGetter<PlacedFeature> placedFeatures) implements BiomeModifier {

    private record Addition(TagKey<Biome> biomes, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
    }

    private static final List<Addition> ADDITIONS = new ArrayList<>();

    public static void addFeature(TagKey<Biome> biomes, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
        ADDITIONS.add(new Addition(biomes, step, feature));
    }

    public static final MapCodec<MythicOreBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryOps.retrieveGetter(Registries.PLACED_FEATURE)
    ).apply(instance, MythicOreBiomeModifier::new));

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MythicMetals.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<MythicOreBiomeModifier>> ORES = SERIALIZERS.register("ores", () -> CODEC);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        for (var addition : ADDITIONS) {
            if (!biome.is(addition.biomes())) continue;

            var feature = this.placedFeatures.get(addition.feature());
            if (feature.isEmpty()) {
                MythicMetals.LOGGER.error("[Mythic Metals] Placed feature {} is missing, ore will not generate", addition.feature().location());
                continue;
            }
            builder.getGenerationSettings().addFeature(addition.step(), feature.get());
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
