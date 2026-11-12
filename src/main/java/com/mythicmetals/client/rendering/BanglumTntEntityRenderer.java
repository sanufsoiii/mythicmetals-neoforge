package com.mythicmetals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.entity.BanglumTntEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

// [VanillaCopy] of the TntEntityRenderer, but renders our texture
public class BanglumTntEntityRenderer extends EntityRenderer<BanglumTntEntity> {
    private final BlockRenderDispatcher blockRenderManager;

    public BanglumTntEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        blockRenderManager = context.getBlockRenderDispatcher();
    }

    public void render(BanglumTntEntity banglumTnt, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.5, 0.0);
        int j = banglumTnt.getFuse();
        if ((float) j - g + 1.0F < 10.0F) {
            float h = 1.0F - ((float) j - g + 1.0F) / 10.0F;
            h = Mth.clamp(h, 0.0F, 1.0F);
            h *= h;
            h *= h;
            float k = 1.0F + h * 0.3F;
            matrixStack.scale(k, k, k);
        }

        matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrixStack.translate(-0.5, -0.5, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderManager, MythicBlocks.BANGLUM_TNT_BLOCK.defaultBlockState(), matrixStack, vertexConsumerProvider, i, j / 5 % 2 == 0);
        matrixStack.popPose();
        super.render(banglumTnt, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTextureLocation(BanglumTntEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;

    }

}
