package com.mythicmetals.block;

import com.mythicmetals.misc.UsefulSingletonForColorUtil;
import io.wispforest.owo.particles.ClientParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class StarriteOreBlock extends DropExperienceBlock {
    public StarriteOreBlock(Properties settings, UniformInt uniformIntProvider) {
        super(uniformIntProvider, settings);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        ClientParticles.spawnCenteredOnBlock(new DustParticleOptions(new Vector3f(UsefulSingletonForColorUtil.MetalColors.STARRITE.hsv()), 1F), world, pos, 2.0D);
    }
}
