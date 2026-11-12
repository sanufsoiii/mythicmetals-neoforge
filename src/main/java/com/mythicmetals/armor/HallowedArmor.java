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

public class HallowedArmor extends ArmorItem {

    @Environment(EnvType.CLIENT)
    private HumanoidModel<LivingEntity> model;
    public final ArmorItem.Type type;

    public HallowedArmor(ArmorItem.Type type, Properties settings) {
        this(MythicArmorMaterials.HALLOWED, type, settings);
    }

    public HallowedArmor(ArmorMaterial material, ArmorItem.Type slot, Properties settings) {
        super(RegistryHelper.getEntry(material), slot, settings);
        this.type = slot;
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
        var root = models.bakeLayer(MythicModelHandler.HALLOWED_ARMOR);
        return new HelmetModel(root, slot);
    }

    @NotNull
    public ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return RegistryHelper.id("textures/models/hallowed_model.png");
    }
}
