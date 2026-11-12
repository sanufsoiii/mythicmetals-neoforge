package com.mythicmetals.client.rendering;

import com.mythicmetals.entity.StarPlatinumArrowEntity;
import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class StarPlatinumArrowEntityRenderer extends ArrowRenderer<StarPlatinumArrowEntity> {
    public static final ResourceLocation TEXTURE = RegistryHelper.id("textures/models/star_platinum_arrow.png");

    public StarPlatinumArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(StarPlatinumArrowEntity entity) {
        return TEXTURE;
    }
}
