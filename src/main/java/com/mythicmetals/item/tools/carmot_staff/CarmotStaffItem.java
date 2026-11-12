package com.mythicmetals.item.tools.carmot_staff;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;

import static com.mythicmetals.component.MythicDataComponents.CARMOT_STAFF_BLOCK;
import static com.mythicmetals.component.MythicDataComponents.LOCKED;

@Deprecated(forRemoval = true, since = "0.23.0")
public class CarmotStaffItem extends TieredItem {


    public CarmotStaffItem(Tier material, Properties settings) {
        super(material, settings);
    }

    @Deprecated(forRemoval = true, since = "0.23.0")
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (stack.has(CARMOT_STAFF_BLOCK) && !stack.getOrDefault(LOCKED, false) && entity.isAlwaysTicking()) {
            var player = (Player) entity;
            var component = stack.get(CARMOT_STAFF_BLOCK);
            assert component != null;
            var blockItem = component.block().asItem();
            player.getInventory().placeItemBackInInventory(new ItemStack(blockItem));
            stack.remove(CARMOT_STAFF_BLOCK);
        }
    }
}
