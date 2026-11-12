package com.mythicmetals.port.assets;

import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

/**
 * Finds the original Mythic Metals jar (downloaded by the player) in {@code <gameDir>/resourcepacks/}.
 * <p>
 * Only that folder is scanned, without recursion; an archive qualifies when it contains {@link #MARKER}.
 * The result is cached until {@link #rescan()}. Nothing is downloaded: the port never touches the network.
 */
public final class OriginalAssetsLocator {

    static final Logger LOGGER = LoggerFactory.getLogger("mythicmetals-port");

    public static final String MARKER = "assets/mythicmetals/textures/item/adamantite_ingot.png";

    private static Optional<Path> cached = null;

    private OriginalAssetsLocator() {
    }

    public static synchronized Optional<Path> find() {
        if (cached == null) {
            cached = scan();
        }
        return cached;
    }

    /**
     * Drops the cached result and scans again (the "Check again" button).
     */
    public static synchronized Optional<Path> rescan() {
        cached = null;
        return find();
    }

    private static Optional<Path> scan() {
        Path dir = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
        List<Path> matches = new ArrayList<>();

        if (Files.isDirectory(dir)) {
            try (Stream<Path> files = Files.list(dir)) {
                for (Path file : files.filter(Files::isRegularFile).toList()) {
                    String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
                    if ((name.endsWith(".jar") || name.endsWith(".zip")) && containsMarker(file)) {
                        matches.add(file);
                    }
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.warn("[mythicmetals-port] Could not list {}", dir, e);
            }
        }

        if (matches.isEmpty()) {
            LOGGER.info("[mythicmetals-port] ORIGINAL_ASSETS_MISSING");
            return Optional.empty();
        }

        matches.sort(Comparator.comparing(OriginalAssetsLocator::lastModified).reversed());
        Path chosen = matches.get(0);
        for (Path other : matches.subList(1, matches.size())) {
            LOGGER.info("[mythicmetals-port] Ignoring older original jar {} (using {})", other.getFileName(), chosen.getFileName());
        }
        LOGGER.info("[mythicmetals-port] ORIGINAL_ASSETS_FOUND {}", chosen.getFileName());
        return Optional.of(chosen);
    }

    private static boolean containsMarker(Path file) {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.getEntry(MARKER) != null;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[mythicmetals-port] Could not read {}", file.getFileName(), e);
            return false;
        }
    }

    private static FileTime lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file);
        } catch (IOException e) {
            return FileTime.fromMillis(0);
        }
    }
}
