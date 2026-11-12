package com.mythicmetals.armor;

import com.mythicmetals.client.models.HelmetModel;
import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.misc.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BanglumArmor extends HallowedArmor {

    @Environment(EnvType.CLIENT)
    private HumanoidModel<LivingEntity> model;
    public final ArmorItem.Type type;

    public BanglumArmor(ArmorItem.Type type, Properties settings) {
        this(MythicArmorMaterials.LEGENDARY_BANGLUM, type, settings);
    }

    public BanglumArmor(ArmorMaterial material, ArmorItem.Type type, Properties settings) {
        super(material, type, settings);
        this.type = type;
    }

    @Environment(EnvType.CLIENT)
    public HumanoidModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(type.getSlot());
        }
        return model;
    }

    @Environment(EnvType.CLIENT)
    protected HumanoidModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = Minecraft.getInstance().getEntityModels();
        var root = models.bakeLayer(MythicModelHandler.BANGLUM);
        return new HelmetModel(root, slot);
    }

    @NotNull
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return RegistryHelper.id("textures/models/banglum_model.png");
    }
}
