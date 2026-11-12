package com.mythicmetals.entity;

import com.mythicmetals.misc.RegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MythicEntities {
    public static final EntityType<BanglumTntEntity> BANGLUM_TNT_ENTITY_TYPE;
    public static final EntityType<BanglumNukeEntity> BANGLUM_NUKE_ENTITY_TYPE;
    public static final EntityType<StarPlatinumArrowEntity> STAR_PLATINUM_ARROW_ENTITY_TYPE;
    public static final EntityType<RuniteArrowEntity> RUNITE_ARROW_ENTITY_TYPE;
    public static final EntityType<BanglumTntMinecartEntity> BANGLUM_TNT_MINECART_ENTITY_TYPE;
    public static final EntityType<PalladiumMinecartEntity> PALLADIUM_MINECART_ENTITY_TYPE;

    public static void init() {
        RegistryHelper.entityType("banglum_tnt_minecart", BANGLUM_TNT_MINECART_ENTITY_TYPE);
        RegistryHelper.entityType("palladium_minecart", PALLADIUM_MINECART_ENTITY_TYPE);
        RegistryHelper.entityType("banglum_tnt", BANGLUM_TNT_ENTITY_TYPE);
        RegistryHelper.entityType("banglum_nuke", BANGLUM_NUKE_ENTITY_TYPE);
        RegistryHelper.entityType("star_platinum_arrow", STAR_PLATINUM_ARROW_ENTITY_TYPE);
        RegistryHelper.entityType("runite_arrow", RUNITE_ARROW_ENTITY_TYPE);

    }

    static {
        BANGLUM_TNT_MINECART_ENTITY_TYPE = EntityType.Builder.<BanglumTntMinecartEntity>of(
                BanglumTntMinecartEntity::new, MobCategory.MISC)
            .sized(0.98f, 0.7f)
            .clientTrackingRange(8)
            .build();

        PALLADIUM_MINECART_ENTITY_TYPE = EntityType.Builder.<PalladiumMinecartEntity>of(
                PalladiumMinecartEntity::new, MobCategory.MISC)
            .sized(0.98f, 0.7f)
            .clientTrackingRange(8)
            .fireImmune()
            .build();

        BANGLUM_TNT_ENTITY_TYPE = EntityType.Builder.<BanglumTntEntity>of(
            BanglumTntEntity::new, MobCategory.MISC).sized(1f, 1f).build();

        BANGLUM_NUKE_ENTITY_TYPE = EntityType.Builder.<BanglumNukeEntity>of(
            BanglumNukeEntity::new, MobCategory.MISC).sized(3f, 3f).build();

        STAR_PLATINUM_ARROW_ENTITY_TYPE = EntityType.Builder.<StarPlatinumArrowEntity>of(
                StarPlatinumArrowEntity::new, MobCategory.MISC)
            .sized(.5f, .5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build();

        RUNITE_ARROW_ENTITY_TYPE = EntityType.Builder.<RuniteArrowEntity>of(
                RuniteArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build();
    }
}
