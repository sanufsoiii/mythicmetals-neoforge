package com.mythicmetals.item;

import io.wispforest.owo.particles.systems.ParticleSystem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ParticleStick<T> extends Item {
    private final ParticleSystem<T> particle;
    private final T extraData;

    public ParticleStick(Properties settings, ParticleSystem<T> particle) {
        super(settings);
        this.particle = particle;
        this.extraData = null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        if (this.extraData != null) {
            particle.spawn(world, user.position(), extraData);
        } else {
            particle.spawn(world, user.position());
        }
        return InteractionResultHolder.pass(stack);
    }
}
