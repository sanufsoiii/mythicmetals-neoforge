package com.mythicmetals.misc;

import com.mojang.authlib.GameProfile;
import com.mythicmetals.data.MythicTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Block.dropResources;

public final class EpicExplosion {
    private EpicExplosion() {

    }

    /**
     * Cause a large explosion
     *
     * @param world          World where the explosion happened
     * @param x              X-cord for the explosion center
     * @param y              Y-cord for the explosion center
     * @param z              Z-cord for the explosion center
     * @param radius         Explosion radius
     * @param statePredicate Blockstate predicate for filtering out specific blocks
     * @param exploder       Entity which caused the explosion
     * @param cause          PlayerEntity which triggered the explosion, used to check against claim protection
     */
    public static void explode(ServerLevel world, int x, int y, int z, int radius, Predicate<BlockState> statePredicate,
                               @Nullable Entity exploder, @Nullable Player cause) {
        int radiusSq = radius * radius;
        var pos = new BlockPos.MutableBlockPos();
        Explosion explosion = null;

        if (exploder != null) {
            explosion = new Explosion(world, exploder, x, y, z, radius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
        }

        MythicParticleSystem.EXPLOSIVE_EXPLOSION.spawn(world, new Vec3(x, y, z), (float) radius);

        GameProfile gameProfile = cause != null ? cause.getGameProfile() : Protection.UNKNOWN;

        for (int ox = -radius; ox < radius; ox++) {
            for (int oy = -radius; oy < radius; oy++) {
                for (int oz = -radius; oz < radius; oz++) {
                    if (ox * ox + oy * oy + oz * oz > radiusSq) continue;

                    pos.set(x + ox, y + oy, z + oz);
                    var state = world.getBlockState(pos);

                    if (state.isAir() || state.getBlock().getExplosionResistance() > 10000) continue;

                    if (!statePredicate.test(state)) continue;

                    if (explosion != null) {
                        if (BlockBreaker.isProtected(world, pos, explosion, gameProfile, cause)) continue;
                    } else {
                        if (BlockBreaker.isProtected(world, pos, gameProfile, cause)) continue;
                    }

                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

    }

    /**
     * Absorbs water around a center point in a given radius
     *
     * @param world  World where the explosion happened
     * @param x      X-cord for the center of this interaction
     * @param y      Y-cord for the center of this interaction
     * @param z      Z-cord for the center of this interaction
     * @param radius Water absorption radius
     * @param cause  PlayerEntity which triggered this, used to check against claim protection
     */
    public static void absorbWater(ServerLevel world, int x, int y, int z, int radius, @Nullable Player cause) {
        int radiusSq = radius * radius;
        var pos = new BlockPos.MutableBlockPos();

        GameProfile playerId = cause != null ? cause.getGameProfile() : Protection.UNKNOWN;

        for (int ox = -radius; ox < radius; ox++) {
            for (int oy = -radius; oy < radius; oy++) {
                for (int oz = -radius; oz < radius; oz++) {
                    if (ox * ox + oy * oy + oz * oz > radiusSq) continue;

                    pos.set(x + ox, y + oy, z + oz);

                    if (!Protection.canBreakBlock(world, pos, playerId, cause)) continue;

                    var state = world.getBlockState(pos);
                    var fluidState = world.getFluidState(pos);

                    if (fluidState.is(FluidTags.WATER)) {
                        if (state.getBlock() instanceof BucketPickup drainable && drainable.pickupBlock(cause, world, pos, state).isEmpty()) {
                            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        } else if (state.is(MythicTags.SPONGABLES)) {
                            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                            dropResources(state, world, pos, blockEntity);
                            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }

    }
}
