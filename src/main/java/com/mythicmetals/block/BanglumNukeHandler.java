package com.mythicmetals.block;

import com.mythicmetals.data.MythicTags;
import com.mythicmetals.entity.BanglumNukeEntity;
import com.mythicmetals.registry.RegisterSounds;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.gameevent.GameEvent;

public class BanglumNukeHandler {
    public static void init() {
        // Fabric UseBlockCallback -> PlayerInteractEvent.RightClickBlock; a non-PASS result cancels the interaction
        NeoForge.EVENT_BUS.<PlayerInteractEvent.RightClickBlock>addListener(event -> {
            var result = onUseBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    private static InteractionResult onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        {
            var stack = player.getItemInHand(hand);

            if (!stack.is(Items.FLINT_AND_STEEL)) return InteractionResult.PASS;

            var targetBlock = world.getBlockState(hitResult.getBlockPos());

            if (!targetBlock.is(MythicBlocks.BANGLUM.getStorageBlock())
                && !targetBlock.is(MythicBlocks.MORKITE.getStorageBlock()))
                return InteractionResult.PASS;

            var pos = hitResult.getBlockPos();

            if (tryLightBigTntAt(world, player, pos.getX(), pos.getY(), pos.getZ())) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }
    }

    public static boolean tryLightBigTntWithDispenser(BlockSource dispenser) {
        var world = dispenser.level();
        BlockState state = world.getBlockState(dispenser.pos().relative(dispenser.state().getValue(DispenserBlock.FACING)));
        var pos = dispenser.pos().relative(dispenser.state().getValue(DispenserBlock.FACING));

        if (!state.is(MythicBlocks.BANGLUM.getStorageBlock())
            && !state.is(MythicBlocks.MORKITE.getStorageBlock()))
            return false;

        return tryLightBigTntAt(world, null, pos.getX(), pos.getY(), pos.getZ());
    }

    private static boolean tryLightBigTntAt(Level world, Player player, int x, int y, int z) {
        if (world.isClientSide()) {
            return false;
        }
        var match = getBanglumNukePattern().find(world, new BlockPos(x, y, z));
        if (match != null) {
            var startPos = match.getBlock(0, 0, 0);
            var endPos = match.getBlock(2, 2, 2);
            for (BlockPos pos : BlockPos.betweenClosed(startPos.getPos(), endPos.getPos())) {
                world.removeBlock(pos, false);
                world.blockUpdated(pos, Blocks.AIR);
            }

            BanglumNukeEntity nuke = new BanglumNukeEntity(world, x + 1.5, y, z + 1.5, player, match.getBlock(1, 1, 1).getState().getBlock());
            world.addFreshEntity(nuke);
            world.playSound(
                null, nuke.getX(), nuke.getY(), nuke.getZ(), RegisterSounds.BANGLUM_NUKE_IGNITE, SoundSource.BLOCKS, 1.0F, 1.0F
            );
            world.gameEvent(player, GameEvent.PRIME_FUSE, new BlockPos(x, y, z));
            return true;
        }

        return false;
    }

    public static BlockPattern getBanglumNukePattern() {
        return BlockPatternBuilder.start()
            .aisle("BMB", "MBM", "BMB")
            .aisle("MBM", "BCB", "MBM")
            .aisle("BMB", "MBM", "BMB")
            .where('B', cachedBlockPos -> {
                return cachedBlockPos != null && cachedBlockPos.getState().equals(MythicBlocks.BANGLUM.getStorageBlock().defaultBlockState());
            })
            .where('M', cachedBlockPos -> {
                return cachedBlockPos != null && cachedBlockPos.getState().equals(MythicBlocks.MORKITE.getStorageBlock().defaultBlockState());
            })
            .where('C', cachedBlockPosition -> cachedBlockPosition != null && cachedBlockPosition.getState().is(MythicTags.NUKE_CORES))
            .build();
    }
}
