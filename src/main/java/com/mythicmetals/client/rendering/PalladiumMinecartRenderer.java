package com.mythicmetals.client.rendering;

import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.entity.PalladiumMinecartEntity;
import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;

public class PalladiumMinecartRenderer extends MinecartRenderer<PalladiumMinecartEntity> {

    public PalladiumMinecartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, MythicModelHandler.PALLADIUM_MINECART);
    }

    @Override
    public ResourceLocation getTexture(PalladiumMinecartEntity abstractMinecartEntity) {
        return RegistryHelper.id("textures/models/palladium_minecart.png");
    }
}
