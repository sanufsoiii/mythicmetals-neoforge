package com.mythicmetals.port.assets;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.fml.loading.FMLPaths;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;

/**
 * Shown once on the title screen when the original jar is not in {@code resourcepacks/}.
 * Buttons only open web pages / the folder in the system; the mod itself downloads nothing.
 */
public class MissingAssetsScreen extends Screen {

    public static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/mythicmetals/files";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/mythicmetals/version/0.24.6+1.21";

    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW = 22;

    private final Screen parent;
    private List<FormattedCharSequence> bodyLines = List.of();
    private int bodyTop;
    private int statusY;
    private boolean showNotFound = false;

    public MissingAssetsScreen(Screen parent) {
        super(Component.translatable("mythicmetals.port.assets.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int left = centerX - BUTTON_WIDTH / 2;

        this.bodyLines = this.font.split(Component.translatable("mythicmetals.port.assets.text"), Math.min(this.width - 40, 360));
        this.bodyTop = 28;
        int y = this.bodyTop + this.bodyLines.size() * 10 + 8;

        this.addRenderableWidget(Button.builder(Component.translatable("mythicmetals.port.assets.download_curseforge"),
            b -> Util.getPlatform().openUri(URI.create(CURSEFORGE_URL))).bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        y += ROW;
        this.addRenderableWidget(Button.builder(Component.translatable("mythicmetals.port.assets.download_modrinth"),
            b -> Util.getPlatform().openUri(URI.create(MODRINTH_URL))).bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        y += ROW;
        this.addRenderableWidget(Button.builder(Component.translatable("mythicmetals.port.assets.open_folder"),
            b -> openResourcePacksFolder()).bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        y += ROW;
        this.addRenderableWidget(Button.builder(Component.translatable("mythicmetals.port.assets.check_again"),
            b -> checkAgain()).bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        y += ROW;
        this.addRenderableWidget(Button.builder(Component.translatable("mythicmetals.port.assets.continue"),
            b -> this.onClose()).bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        y += ROW + 2;

        this.addRenderableWidget(Checkbox.builder(Component.translatable("mythicmetals.port.assets.dont_show_again"), this.font)
            .pos(left, y)
            .selected(AssetNoticeSettings.dontShowAgain())
            .onValueChange((checkbox, value) -> AssetNoticeSettings.setDontShowAgain(value))
            .build());
        this.statusY = y + 24;
    }

    private void openResourcePacksFolder() {
        try {
            var folder = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
            Files.createDirectories(folder);
            Util.getPlatform().openPath(folder);
        } catch (Exception e) {
            OriginalAssetsLocator.LOGGER.warn("[mythicmetals-port] Could not open the resourcepacks folder", e);
        }
    }

    private void checkAgain() {
        if (OriginalAssetsLocator.rescan().isPresent()) {
            if (this.minecraft != null) {
                this.minecraft.getResourcePackRepository().reload();
                this.minecraft.reloadResourcePacks();
                this.minecraft.setScreen(this.parent);
            }
        } else {
            this.showNotFound = true;
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderMenuBackground(graphics);
        graphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFAA00);
        int y = this.bodyTop;
        for (FormattedCharSequence line : this.bodyLines) {
            graphics.drawCenteredString(this.font, line, this.width / 2, y, 0xFFFFFFFF);
            y += 10;
        }
        if (this.showNotFound) {
            graphics.drawCenteredString(this.font, Component.translatable("mythicmetals.port.assets.not_found").withStyle(ChatFormatting.RED), this.width / 2, this.statusY, 0xFFFF5555);
        }
    }

    @Override
    public void onClose() {
        // Esc = "Continue without assets"
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
