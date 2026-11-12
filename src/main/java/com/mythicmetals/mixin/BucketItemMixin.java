package com.mythicmetals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mythicmetals.block.Lavaloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Shadow @Final private Fluid content;

    @Shadow protected abstract void playEmptySound(@Nullable Player player, LevelAccessor world, BlockPos pos);

    @ModifyVariable(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"), ordinal = 1)
    private BlockPos mythicmetals$targetBlockOnLava(BlockPos original, Level world, Player user, InteractionHand hand, @Local BlockState blockState, @Local BlockHitResult blockHitResult) {
        if (blockState.getBlock() instanceof Lavaloggable && this.content.equals(Fluids.LAVA)) {
            return blockHitResult.getBlockPos();
        }
        return original;
    }

    // NeoForge: the logic lives in the 5-arg emptyContents (with the container stack); Yarn isLiquid = Mojang liquid
    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;liquid()Z"), cancellable = true)
    private void mythicmetals$fillLavalog(Player player, Level world, BlockPos pos, BlockHitResult hitResult, net.minecraft.world.item.ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        var state = world.getBlockState(pos);
        if (this.content.equals(Fluids.LAVA) && state.getBlock() instanceof Lavaloggable lavaloggable) {
            // TODO - Vanilla behavior here is to eat the fluid if you log the same block twice
            // Try and explore whether you can prevent placing lava in the same block twice
            // Lava is mildly more inconvenient to source, after all
            lavaloggable.placeLiquid(world, pos, state, Fluids.LAVA.getSource(false));
            this.playEmptySound(player, world, pos);
            cir.setReturnValue(true);
        }
    }
}
