package com.mythicmetals.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class HelmetModel extends HumanoidModel<LivingEntity> {
    final EquipmentSlot slot;

    public HelmetModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }

    @Override
    public void prepareMobModel(LivingEntity livingEntity, float f, float g, float h) {
        super.prepareMobModel(livingEntity, f, g, h);
    }

    @Override
    public void setupAnim(LivingEntity entity, float f, float g, float h, float i, float j) {
        if (!(entity instanceof ArmorStand stand)) {
            super.setupAnim(entity, f, g, h, i, j);
            return;
        }
        this.head.xRot = ((float) Math.PI / 180F) * stand.getHeadPose().getX();
        this.head.yRot = ((float) Math.PI / 180F) * stand.getHeadPose().getY();
        this.head.zRot = ((float) Math.PI / 180F) * stand.getHeadPose().getZ();
        this.head.setPos(0.0F, 1.0F, 0.0F);
        this.body.xRot = ((float) Math.PI / 180F) * stand.getBodyPose().getX();
        this.body.yRot = ((float) Math.PI / 180F) * stand.getBodyPose().getY();
        this.body.zRot = ((float) Math.PI / 180F) * stand.getBodyPose().getZ();
        this.leftArm.xRot = ((float) Math.PI / 180F) * stand.getLeftArmPose().getX();
        this.leftArm.yRot = ((float) Math.PI / 180F) * stand.getLeftArmPose().getY();
        this.leftArm.zRot = ((float) Math.PI / 180F) * stand.getLeftArmPose().getZ();
        this.rightArm.xRot = ((float) Math.PI / 180F) * stand.getRightArmPose().getX();
        this.rightArm.yRot = ((float) Math.PI / 180F) * stand.getRightArmPose().getY();
        this.rightArm.zRot = ((float) Math.PI / 180F) * stand.getRightArmPose().getZ();
        this.leftLeg.xRot = ((float) Math.PI / 180F) * stand.getLeftLegPose().getX();
        this.leftLeg.yRot = ((float) Math.PI / 180F) * stand.getLeftLegPose().getY();
        this.leftLeg.zRot = ((float) Math.PI / 180F) * stand.getLeftLegPose().getZ();
        this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
        this.rightLeg.xRot = ((float) Math.PI / 180F) * stand.getRightLegPose().getX();
        this.rightLeg.yRot = ((float) Math.PI / 180F) * stand.getRightLegPose().getY();
        this.rightLeg.zRot = ((float) Math.PI / 180F) * stand.getRightLegPose().getZ();
        this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
        this.hat.copyFrom(head);
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
        renderArmorPart(slot);
        super.renderToBuffer(ms, buffer, light, overlay, color);
    }

    private void renderArmorPart(EquipmentSlot slot) {
        setAllVisible(false);
        if (slot == EquipmentSlot.HEAD) {
            head.visible = true;
        }
    }


}
