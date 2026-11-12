package com.mythicmetals.entity;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.item.tools.MythicTools;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BanglumTntMinecartEntity extends MinecartTNT {
    private static final double MAX_POWER = 8.0;
    private int fuseTicks = -1;

    public BanglumTntMinecartEntity(EntityType<? extends MinecartTNT> entityType, Level world) {
        super(entityType, world);
    }

    public BanglumTntMinecartEntity(Level world, double x, double y, double z) {
        this(MythicEntities.BANGLUM_TNT_MINECART_ENTITY_TYPE, world);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MythicBlocks.BANGLUM_TNT_BLOCK.defaultBlockState();
    }

    @Override
    protected Item getDropItem() {
        return MythicTools.BANGLUM_TNT_MINECART;
    }

    // [VanillaCopy], but increases the power cap to 8
    @Override
    protected void explode(@Nullable DamageSource damageSource, double power) {
        if (!this.level().isClientSide) {
            double d = Math.sqrt(power);
            if (d > MAX_POWER) {
                d = MAX_POWER;
            }

            this.level().explode(this, damageSource, null, this.getX(), this.getY(), this.getZ(), (float) (4.0 + this.random.nextDouble() * 1.5 * d), false, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fuseTicks > 0) {
            --this.fuseTicks;
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        } else if (this.fuseTicks == 0) {
            this.explode(this.getDeltaMovement().horizontalDistanceSqr());
        }

        if (this.horizontalCollision) {
            double d = this.getDeltaMovement().horizontalDistanceSqr();
            if (d >= 0.01F) {
                this.explode(d);
            }
        }
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean powered) {
        if (powered && this.fuseTicks < 0) {
            this.primeFuse();
        }
    }

    @Override
    public void destroy(DamageSource damageSource) {
        double d = this.getDeltaMovement().horizontalDistanceSqr();
        if (!damageSource.is(DamageTypeTags.IS_FIRE) && !damageSource.is(DamageTypeTags.IS_EXPLOSION) && !(d >= 0.01F)) {
            super.destroy(damageSource);
        } else {
            if (this.fuseTicks < 0) {
                this.primeFuse();
                this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
            }
        }
    }

    @Override
    public int getFuse() {
        return this.fuseTicks;
    }

    @Override
    public boolean isPrimed() {
        return this.fuseTicks > -1;
    }

    @Override
    public void primeFuse() {
        this.fuseTicks = 120;
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, EntityEvent.EAT_GRASS);
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 0.8F);
            }
        }
    }

    @Override
    public Type getMinecartType() {
        return MythicMetals.BANGLUM_TNT;
    }
}
