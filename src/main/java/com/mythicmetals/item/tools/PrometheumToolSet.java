package com.mythicmetals.item.tools;

import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.component.PrometheumComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class PrometheumToolSet extends ToolSet {

    public PrometheumToolSet(Tier material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected PickaxeItem makePickaxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PrometheumPick(material, settings
            .attributes(createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected SwordItem makeSword(Tier material, int damage, float speed, Item.Properties settings) {
        return new PrometheumSword(material, settings
            .attributes(createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected AxeItem makeAxe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PrometheumAxe(material, settings
            .attributes(createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected HoeItem makeHoe(Tier material, int damage, float speed, Item.Properties settings) {
        return new PrometheumHoe(material, settings
            .attributes(createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected ShovelItem makeShovel(Tier material, int damage, float speed, Item.Properties settings) {
        return new PrometheumShovel(material, settings
            .attributes(createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    public static class PrometheumAxe extends AxeItem implements AutoRepairable {
        public PrometheumAxe(Tier material, Item.Properties settings) {
            super(material, settings);
        }

    }

    public static class PrometheumHoe extends HoeItem implements AutoRepairable {
        public PrometheumHoe(Tier material, Item.Properties settings) {
            super(material, settings);
        }

    }

    public static class PrometheumPick extends PickaxeItem implements AutoRepairable {
        public PrometheumPick(Tier material, Item.Properties settings) {
            super(material, settings);
        }

    }

    public static class PrometheumShovel extends ShovelItem implements AutoRepairable {
        public PrometheumShovel(Tier material, Item.Properties settings) {
            super(material, settings);
        }

    }

    public static class PrometheumSword extends SwordItem implements AutoRepairable {
        public PrometheumSword(Tier material, Item.Properties settings) {
            super(material, settings);
        }

    }

}
