package com.mythicmetals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.block.entity.EnchantedMidasGoldBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.RandomSource;

public class EnchantedMidasBlockEntityRenderer implements BlockEntityRenderer<EnchantedMidasGoldBlockEntity> {
    private final BlockRenderDispatcher blockRenderManager;

    public EnchantedMidasBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        blockRenderManager = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(EnchantedMidasGoldBlockEntity midasBlockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        blockRenderManager.renderBatched(
            MythicBlocks.MIDAS_GOLD.getStorageBlock().defaultBlockState(),
            midasBlockEntity.getBlockPos(),
            midasBlockEntity.getLevel(),
            matrices,
            ItemRenderer.getFoilBuffer(vertexConsumers, RenderType.cutoutMipped(), true, true),
            true,
            RandomSource.create());
        matrices.popPose();
    }
}
