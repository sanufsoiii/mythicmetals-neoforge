package com.mythicmetals.port.assets;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Registers the built-in, always enabled, top-most client resource pack built from the original jar.
 * Registered from the client entry point only; nothing happens for server data packs.
 */
public final class OriginalAssetsPack {

    public static final String PACK_ID = "mythicmetals:original_assets";

    private OriginalAssetsPack() {
    }

    public static void register(IEventBus modBus) {
        modBus.<AddPackFindersEvent>addListener(event -> {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

            // Scanning happens inside the source, so PackRepository#reload() picks up a jar added later
            // (after OriginalAssetsLocator#rescan()).
            event.addRepositorySource(consumer -> OriginalAssetsLocator.find().ifPresent(jar -> consumer.accept(createPack(jar))));
        });
    }

    private static Pack createPack(Path jar) {
        var info = new PackLocationInfo(PACK_ID, Component.translatable("mythicmetals.port.assets.pack_name"), PackSource.BUILT_IN, Optional.empty());

        var resources = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return new OriginalAssetsPackResources(location, new FilePackResources.SharedZipFileAccess(jar.toFile()));
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                return openPrimary(location);
            }
        };

        var metadata = new Pack.Metadata(
                Component.translatable("mythicmetals.port.assets.pack_description"),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                List.of());

        return new Pack(info, resources, metadata, new PackSelectionConfig(true, Pack.Position.TOP, true));
    }
}
