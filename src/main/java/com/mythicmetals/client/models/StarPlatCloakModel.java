package com.mythicmetals.client.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class StarPlatCloakModel {
    public static final ModelPart CAPE_MODEL = createCape();

    private static ModelPart createCape() {
        MeshDefinition data = new MeshDefinition();
        var root = data.getRoot();

        root.addOrReplaceChild(
            "cape_bone",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-5.5F, 0.0F, -0.05F, 11.0F, 23.0F, 1.0F),
            PartPose.offset(0.0F, 0.5F, 2.9F)
        );

        return data.getRoot().bake(32, 32);
    }

}
