package com.mythicmetals.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * NeoForge replacement for the subset of Common Protection API (Patbox) used by Mythic Metals.
 * <p>
 * There is no common protection API on NeoForge. Claim/protection mods listen to {@link BlockEvent.BreakEvent},
 * so a block check asks them through a <b>probe</b> event that is posted but never acted upon (the block is not
 * broken by it). Without a player the check falls back to "allowed", like CPA without a protection provider.
 */
public final class Protection {

    /**
     * Stand-in for {@code CommonProtection.UNKNOWN}: the profile used when no player caused the action.
     */
    public static final GameProfile UNKNOWN = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), "[Mythic Metals]");

    private static final ThreadLocal<Boolean> PROBING = ThreadLocal.withInitial(() -> false);

    /**
     * @return true while a protection probe {@link BlockEvent.BreakEvent} is being posted;
     * our own break handlers must ignore such events.
     */
    public static boolean isProbing() {
        return PROBING.get();
    }

    public static boolean canBreakBlock(Level level, BlockPos pos, GameProfile profile, @Nullable Player player) {
        if (player == null) return true;
        if (!level.mayInteract(player, pos)) return false;

        PROBING.set(true);
        try {
            var event = NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player));
            return !event.isCanceled();
        } finally {
            PROBING.set(false);
        }
    }

    public static boolean canExplodeBlock(Level level, BlockPos pos, @Nullable Explosion explosion, GameProfile profile, @Nullable Player player) {
        return canBreakBlock(level, pos, profile, player);
    }

    public static boolean canDamageEntity(Level level, Entity entity, GameProfile profile, @Nullable Player player) {
        if (player == null) return true;
        if (entity instanceof Player target) return player.canHarmPlayer(target);
        return true;
    }

    private Protection() {
    }
}
