package com.mythicmetals.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// [ConjuringCopy] https://github.com/wisp-forest/conjuring/blob/1.18/src/main/java/com/glisco/conjuring/mixin/WorldRendererInvoker.java
@Mixin(LevelRenderer.class)
public interface WorldRendererInvoker {

    @Invoker("renderShape")
    static void mythicmetals$drawShapeOutline(
        PoseStack matrixStack,
        VertexConsumer vertexConsumer,
        VoxelShape voxelShape,
        double offsetX,
        double offsetY,
        double offsetZ,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        throw new AssertionError();
    }

}
