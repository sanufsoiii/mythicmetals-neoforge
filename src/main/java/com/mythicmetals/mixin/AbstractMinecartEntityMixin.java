package com.mythicmetals.mixin;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.block.Lavaloggable;
import com.mythicmetals.block.PalladiumRailBlock;
import com.mythicmetals.entity.BanglumTntMinecartEntity;
import com.mythicmetals.entity.PalladiumMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartEntityMixin {

    @Inject(method = "createMinecart", at = @At("HEAD"), cancellable = true)
    private static void mythicmetals$createCustomMinecart(ServerLevel world, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, Player player, CallbackInfoReturnable<AbstractMinecart> cir) {
        if (type.equals(MythicMetals.BANGLUM_TNT)) {
            cir.setReturnValue(new BanglumTntMinecartEntity(world, x, y, z));
        }

        if (type.equals(MythicMetals.PALLADIUM_MINECART)) {
            cir.setReturnValue(new PalladiumMinecartEntity(world, x, y, z));
        }
    }

    @ModifyVariable(method = "moveAlongTrack", at = @At(value = "STORE", ordinal = 0))
    private boolean mythicmetals$boostInLava(boolean original, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof Lavaloggable && PalladiumRailBlock.isLavaLogged(state)) {
            return true;
        }
        return original;
    }
}
