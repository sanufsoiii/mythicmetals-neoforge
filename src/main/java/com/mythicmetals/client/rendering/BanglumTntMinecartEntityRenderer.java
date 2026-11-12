package com.mythicmetals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.entity.BanglumTntMinecartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class BanglumTntMinecartEntityRenderer extends MinecartRenderer<BanglumTntMinecartEntity> {
    private final BlockRenderDispatcher tntBlockRenderManager;

    public BanglumTntMinecartEntityRenderer(EntityRendererProvider.Context context) {
        super(context, MythicModelHandler.BANGLUM_TNT_MINECART);
        this.tntBlockRenderManager = context.getBlockRenderDispatcher();
    }

    protected void renderBlock(
        MinecartTNT tntMinecartEntity, float f, BlockState blockState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i
    ) {
        int j = tntMinecartEntity.getFuse();
        if (j > -1 && (float) j - f + 1.0F < 10.0F) {
            float g = 1.0F - ((float) j - f + 1.0F) / 10.0F;
            g = Mth.clamp(g, 0.0F, 1.0F);
            g *= g;
            g *= g;
            float h = 1.0F + g * 0.3F;
            matrixStack.scale(h, h, h);
        }

        renderFlashingBlock(this.tntBlockRenderManager, blockState, matrixStack, vertexConsumerProvider, i, j > -1 && j / 5 % 2 == 0);
    }

    /**
     * Renders a given block state into the given buffers either normally or with a bright white overlay.
     * Used for rendering primed TNT either standalone or as part of a TNT minecart.
     *
     * @param drawFlash whether a white semi-transparent overlay is added to the block to indicate the flash
     */
    public static void renderFlashingBlock(
        BlockRenderDispatcher blockRenderManager, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, boolean drawFlash
    ) {
        int i;
        if (drawFlash) {
            i = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
        } else {
            i = OverlayTexture.NO_OVERLAY;
        }

        blockRenderManager.renderSingleBlock(state, matrices, vertexConsumers, light, i);
    }
}
