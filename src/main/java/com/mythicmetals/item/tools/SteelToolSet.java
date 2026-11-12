package com.mythicmetals.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SteelToolSet extends ToolSet {
    public SteelToolSet(Tier material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected ShovelItem makeShovel(Tier material, int damage, float speed, Item.Properties settings) {
        return new SteelShovel(material, settings.attributes(createAttributeModifiers(material, damage, speed)));
    }

    public static class SteelShovel extends ShovelItem {
        public SteelShovel(Tier material, Properties settings) {
            super(material, settings);
        }

        @Override
        public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {

            // Special Ability - Shovel Snow for free
            if (!world.isClientSide() && state.is(BlockTags.SNOW)) {
                return true;
            }

            return super.mineBlock(stack, world, state, pos, miner);
        }
    }
}
