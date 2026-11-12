package com.mythicmetals.item.tools;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.misc.RegistryHelper;
import com.mythicmetals.misc.StringUtilsAtHome;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public class ToolSet {

    private final SwordItem sword;
    private final AxeItem axe;
    private final PickaxeItem pickaxe;
    private final ShovelItem shovel;
    private final HoeItem hoe;

    private final List<Float> attackSpeed = new ArrayList<>();

    private static Item.Properties createSettings(Consumer<Item.Properties> settingsProcessor) {
        final var settings = new Item.Properties().group(MythicMetals.TABBED_GROUP).tab(2);
        settingsProcessor.accept(settings);
        return settings;
    }

    public ToolSet(Tier material, int[] damage, float[] speed) {
        this(material, damage, speed, settings -> {
        });
    }

    public ToolSet(Tier material, int[] damage, float[] speed, Consumer<Item.Properties> settingsProcessor) {
        this.sword = this.makeSword(material, damage[0], speed[0], createSettings(settingsProcessor));
        this.axe = this.makeAxe(material, damage[1], speed[1], createSettings(settingsProcessor));
        this.pickaxe = this.makePickaxe(material, damage[2], speed[2], createSettings(settingsProcessor));
        this.shovel = this.makeShovel(material, damage[3], speed[3], createSettings(settingsProcessor));
        this.hoe = this.makeHoe(material, damage[4], speed[4], createSettings(settingsProcessor));
        attackSpeed.add(speed[4]);
        attackSpeed.add(speed[3]);
        attackSpeed.add(speed[2]);
        attackSpeed.add(speed[1]);
        attackSpeed.add(speed[0]);
    }

    public void register(String name) {
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_sword"), sword);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_axe"), axe);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_pickaxe"), pickaxe);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_shovel"), shovel);
        Registry.register(BuiltInRegistries.ITEM, RegistryHelper.id(name + "_hoe"), hoe);
    }

    protected SwordItem makeSword(Tier material, int damage, float speed, Item.Properties settings) {
        return new SwordItem(material, settings.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, damage, speed)));
    }

    protected AxeItem makeAxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new AxeItem(material, settings.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, damage, speed)));
    }

    protected PickaxeItem makePickaxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PickaxeItem(material, settings.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, damage, speed)));
    }

    protected ShovelItem makeShovel(Tier material, int damage, float speed, Item.Properties settings) {
        return new ShovelItem(material, settings.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, damage, speed)));
    }

    protected HoeItem makeHoe(Tier material, int damage, float speed, Item.Properties settings) {
        return new HoeItem(material, settings.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, damage, speed)));
    }

    /**
     * Returns a set of all the ToolItems that make this toolset
     *
     * @return List of ToolItems in order: Sword, Axe, Pickaxe, Shovel, Hoe
     */
    public List<TieredItem> get() {
        return List.of(sword, axe, pickaxe, shovel, hoe);
    }

    public SwordItem getSword() {
        return sword;
    }

    public AxeItem getAxe() {
        return axe;
    }

    public PickaxeItem getPickaxe() {
        return pickaxe;
    }

    public ShovelItem getShovel() {
        return shovel;
    }

    public HoeItem getHoe() {
        return hoe;
    }

    public List<Float> getAttackSpeed() {
        return attackSpeed;
    }

    public static ItemAttributeModifiers createAttributeModifiers(double damage, float speed) {
        if (speed < 0.0f) {
            speed = 0;
        }
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -4.0 + speed, ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }


    public static ItemAttributeModifiers createAttributeModifiers(Tier material, double damage, float speed) {
        return createAttributeModifiers(material.getAttackDamageBonus() + damage, speed);
    }

    public ItemAttributeModifiers.Builder createAttributeBuilder(Tier material, double damage, float speed) {
        if (speed < 0.0f) {
            speed = 0;
        }
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, material.getAttackDamageBonus() + damage, ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -4.0 + speed, ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            );
    }

    public ItemAttributeModifiers createAttributes(Tier material, double damage, float speed) {
        return this.createAttributeBuilder(material, damage, speed).build();
    }

    public String getTitlecaseName() {
        return StringUtilsAtHome.toTitleCase(this.getSword().getTier().toString());
    }
}
