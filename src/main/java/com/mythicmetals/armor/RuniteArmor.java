package com.mythicmetals.armor;

import com.mythicmetals.client.models.HelmetModel;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RuniteArmor extends HallowedArmor {

    private HumanoidModel<LivingEntity> model;
    public final ArmorItem.Type type;

    public RuniteArmor(ArmorItem.Type type, Properties settings) {
        this(MythicArmorMaterials.RUNITE, type, settings);
    }

    public RuniteArmor(ArmorMaterial material, ArmorItem.Type type, Properties settings) {
        super(material, type, settings);
        this.type = type;
    }

    public HumanoidModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(type.getSlot());
        }
        return model;
    }

    protected HumanoidModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = Minecraft.getInstance().getEntityModels();
        var root = models.bakeLayer(MythicModelHandler.RUNITE);
        return new HelmetModel(root, slot);
    }

    @Override
    @NotNull
    public ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return RegistryHelper.id("textures/models/runite_model.png");
    }
}
