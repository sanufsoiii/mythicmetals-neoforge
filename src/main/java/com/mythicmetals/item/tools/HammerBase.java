package com.mythicmetals.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.BlockGetter;

public class HammerBase extends PickaxeItem {

    private final int depth;

    public HammerBase(Tier material, Properties settings, int depth) {
        super(material, settings);
        this.depth = depth;
    }

    public boolean canBreak(ItemStack stack, BlockGetter view, BlockPos pos) {
        return super.isCorrectToolForDrops(stack, view.getBlockState(pos));
    }

    public int getDepth() {
        return depth;
    }
}
