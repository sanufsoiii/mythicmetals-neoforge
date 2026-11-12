package com.mythicmetals.port.assets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The original Mythic Metals jar seen as a resource pack, restricted to client assets.
 * <ul>
 *     <li>{@link PackType#SERVER_DATA}: nothing at all. The port has its own data; recipes, loot and tags of the
 *     original jar must never reach the game.</li>
 *     <li>{@link PackType#CLIENT_RESOURCES}: only {@code assets/<ns>/...} for {@link #ALLOWED_NAMESPACES}.</li>
 *     <li>The jar has no {@code pack.mcmeta}; the pack metadata (pack format 34 = 1.21.1) is supplied here.</li>
 * </ul>
 */
public class OriginalAssetsPackResources extends FilePackResources {

    public static final Set<String> ALLOWED_NAMESPACES = Set.of("mythicmetals", "alloy_forgery");
    public static final int PACK_FORMAT = 34;

    public OriginalAssetsPackResources(PackLocationInfo info, SharedZipFileAccess zip) {
        super(info, zip, "");
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        if (type != PackType.CLIENT_RESOURCES) return Set.of();
        Set<String> namespaces = new HashSet<>(super.getNamespaces(type));
        namespaces.retainAll(ALLOWED_NAMESPACES);
        return namespaces;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES || !ALLOWED_NAMESPACES.contains(location.getNamespace())) return null;
        return super.getResource(type, location);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES || !ALLOWED_NAMESPACES.contains(namespace)) return;
        super.listResources(type, namespace, path, output);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        // Only the optional pack icon; no root files of the mod jar (fabric.mod.json, LICENSE, ...) are exposed.
        if (elements.length == 1 && "pack.png".equals(elements[0])) return super.getRootResource(elements);
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        if (deserializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.translatable("mythicmetals.port.assets.pack_description"), PACK_FORMAT, Optional.empty());
        }
        return null;
    }
}
