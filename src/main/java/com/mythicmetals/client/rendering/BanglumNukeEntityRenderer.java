package com.mythicmetals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.entity.BanglumNukeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

//VanillaCopy of the TntEntityRenderer
public class BanglumNukeEntityRenderer extends EntityRenderer<BanglumNukeEntity> {
    private final BlockRenderDispatcher blockRenderManager;

    public BanglumNukeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        blockRenderManager = context.getBlockRenderDispatcher();
    }

    public void render(BanglumNukeEntity nuke, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.5, 0.0);
        int j = nuke.getFuse();
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

        matrixStack.translate(-1, 0, -1);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    matrixStack.pushPose();
                    matrixStack.translate(x, y, z);

                    BlockState neededState = (x + y + z) % 2 == 0
                        ? MythicBlocks.BANGLUM.getStorageBlock().defaultBlockState()
                        : MythicBlocks.MORKITE.getStorageBlock().defaultBlockState();

                    TntMinecartRenderer.renderWhiteSolidBlock(blockRenderManager, neededState, matrixStack, vertexConsumerProvider, i, j / 5 % 2 == 0);

                    matrixStack.popPose();
                }
            }
        }

        matrixStack.popPose();
        super.render(nuke, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTexture(BanglumNukeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;

    }

}
