package com.mythicmetals.armor;

import com.mythicmetals.client.models.MythicModelHandler;
import com.mythicmetals.client.models.TidesingerBipedModel;
import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.TidesingerPatternComponent;
import com.mythicmetals.misc.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class TidesingerArmor extends HallowedArmor {

    @Environment(EnvType.CLIENT)
    private HumanoidModel<LivingEntity> model;
    public final Type type;

    public TidesingerArmor(Type type, Properties settings) {
        this(MythicArmorMaterials.TIDESINGER, type, settings);
    }

    public TidesingerArmor(ArmorMaterial material, Type slot, Properties settings) {
        super(material, slot, settings.component(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.empty()));
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
    @Override
    protected HumanoidModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = Minecraft.getInstance().getEntityModels();
        var root = models.bakeLayer(MythicModelHandler.TIDESINGER);
        return new TidesingerBipedModel(root, slot);
    }

    // TODO - Feels like magic string, maybe refactor
    @NotNull
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        var component = stack.getOrDefault(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.empty());
        String model = switch (component.pattern()) {
            case "brain" -> "textures/models/tidesinger_model_brain.png";
            case "bubble" -> "textures/models/tidesinger_model_bubble.png";
            case "fire" -> "textures/models/tidesinger_model_fire.png";
            case "horn" -> "textures/models/tidesinger_model_horn.png";
            case "tube" -> "textures/models/tidesinger_model_tube.png";
            default -> "textures/models/tidesinger_model.png";
        };
        return RegistryHelper.id(model);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag type) {
        if (stack.has(MythicDataComponents.TIDESINGER)) {
            stack.get(MythicDataComponents.TIDESINGER).addToTooltip(context, lines::add, type);
        }
    }
}
