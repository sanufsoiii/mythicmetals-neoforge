package com.mythicmetals.port.assets;

import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Replaces the first title screen of the session with {@link MissingAssetsScreen} when the original jar is missing
 * and the player did not tick "Don't show again". In the boot smoke test (-Dmythicmetals.port.exitOnTitle=true)
 * the screen is only logged so the self-check still reaches the title screen.
 */
public final class MissingAssetsNotice {

    private static boolean handledThisSession = false;

    private MissingAssetsNotice() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(MissingAssetsNotice::onScreenOpening);
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (handledThisSession || !(event.getNewScreen() instanceof TitleScreen titleScreen)) return;
        handledThisSession = true;

        if (OriginalAssetsLocator.find().isPresent() || AssetNoticeSettings.dontShowAgain()) return;

        if (Boolean.getBoolean("mythicmetals.port.exitOnTitle")) {
            OriginalAssetsLocator.LOGGER.info("[mythicmetals-port] ASSET_NOTICE_WOULD_SHOW");
            return;
        }

        OriginalAssetsLocator.LOGGER.info("[mythicmetals-port] ASSET_NOTICE_SHOWN");
        event.setNewScreen(new MissingAssetsScreen(titleScreen));
    }
}
