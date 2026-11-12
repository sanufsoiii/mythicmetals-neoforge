package com.mythicmetals.port.assets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * "Don't show again" for the missing assets notice: {@code config/mythicmetals-port-assets.json}
 * ({@code {"dont_show_again": true}}). Deleting the file shows the notice again.
 * Independent from the mod's own config.
 */
public final class AssetNoticeSettings {

    private static final String FILE_NAME = "mythicmetals-port-assets.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private AssetNoticeSettings() {
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
    }

    public static boolean dontShowAgain() {
        Path file = file();
        if (!Files.isRegularFile(file)) return false;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            return json != null && json.has("dont_show_again") && json.get("dont_show_again").getAsBoolean();
        } catch (Exception e) {
            OriginalAssetsLocator.LOGGER.warn("[mythicmetals-port] Could not read {}", file, e);
            return false;
        }
    }

    public static void setDontShowAgain(boolean value) {
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            JsonObject json = new JsonObject();
            json.addProperty("dont_show_again", value);
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            OriginalAssetsLocator.LOGGER.warn("[mythicmetals-port] Could not write {}", file, e);
        }
    }
}
