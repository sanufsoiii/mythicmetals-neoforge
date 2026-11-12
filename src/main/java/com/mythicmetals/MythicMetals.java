package com.mythicmetals;

import com.mythicmetals.ability.Abilities;
import com.mythicmetals.armor.*;
import com.mythicmetals.block.BanglumNukeHandler;
import com.mythicmetals.block.MythicBlocks;
import com.mythicmetals.block.entity.RegisterBlockEntityTypes;
import com.mythicmetals.command.MythicCommands;
import com.mythicmetals.component.MythicDataComponents;
import com.mythicmetals.conditions.MythicResourceConditions;
import com.mythicmetals.config.MythicMetalsConfig;
import com.mythicmetals.data.MythicOreFeatures;
import com.mythicmetals.effects.MythicStatusEffects;
import com.mythicmetals.entity.*;
import com.mythicmetals.item.MythicItems;
import com.mythicmetals.item.MythicPotions;
import com.mythicmetals.item.tools.MythicTools;
import com.mythicmetals.misc.*;
import com.mythicmetals.recipe.MythicRecipeSerializers;
import com.mythicmetals.registry.*;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import com.mythicmetals.component.EntityComponentKey;
import com.mythicmetals.component.MythicAttachments;
import com.mythicmetals.data.MythicOreBiomeModifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MythicMetals.MOD_ID)
public class MythicMetals {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "mythicmetals";
    public static final int CONFIG_VERSION = 13;

    public static final AbstractMinecart.Type BANGLUM_TNT = Enum.valueOf(AbstractMinecart.Type.class, "BANGLUM_TNT");
    public static final AbstractMinecart.Type PALLADIUM_MINECART = Enum.valueOf(AbstractMinecart.Type.class, "PALLADIUM_MINECART");

    public static MythicMetalsConfig CONFIG = MythicMetalsConfig.createAndLoad();

    // owo's OwoItemGroup.Builder#build registers the tab immediately, so it is created at the start of onInitialize(),
    // which runs inside RegisterEvent (on Fabric this was a static final field).
    public static OwoItemGroup TABBED_GROUP;

    private static OwoItemGroup createItemGroup() {
        return OwoItemGroup.builder(RegistryHelper.id("main"), () -> Icon.of(MythicItems.STORMYX.getIngot()))
        .initializer(group -> {
            group.addTab(Icon.of(MythicItems.ADAMANTITE.getIngot()), "items", TagKey.create(Registries.ITEM, RegistryHelper.id("item_tab")), false);
            group.addTab(Icon.of(MythicBlocks.ADAMANTITE.getStorageBlock()), "blocks", TagKey.create(Registries.ITEM, RegistryHelper.id("blocks")), false);
            group.addTab(Icon.of(MythicTools.ADAMANTITE.getPickaxe()), "tools", TagKey.create(Registries.ITEM, RegistryHelper.id("tool_tab")), false);
            group.addTab(Icon.of(MythicArmor.ADAMANTITE.getChestplate()), "armor", TagKey.create(Registries.ITEM, RegistryHelper.id("armor_tab")), false);
            // Port: GitHub/CurseForge/Modrinth point to the unofficial NeoForge port, so its bugs don't land in the original tracker.
            // Discord stays the original author's server. Buttons use own names ("port_*") so their tooltip keys
            // (itemGroup.mythicmetals.main.button.port_*) are not overridden by the original jar's lang mounted from resourcepacks.
            group.addButton(portLinkButton(group, 0, "port_github", "https://github.com/sanufsoiii/mythicmetals-neoforge/issues"));
            group.addButton(portLinkButton(group, 32, "port_curseforge", "https://www.curseforge.com/minecraft/mc-mods/mythicmetals-neoforge"));
            group.addButton(portLinkButton(group, 16, "port_modrinth", "https://modrinth.com/mod/mythicmetals-neoforge"));
            group.addButton(portLinkButton(group, 48, "port_discord", "https://discord.gg/69cKvQWScC"));
        })
        .build();
    }

    // Same icons as owo's ItemGroupButton.github/modrinth/curseforge/discord (owo:textures/gui/icons.png, 16px cells: u = 0/16/32/48).
    private static ItemGroupButton portLinkButton(OwoItemGroup group, int iconU, String name, String url) {
        return ItemGroupButton.link(group, Icon.of(ItemGroupButton.ICONS_TEXTURE, iconU, 0, 64, 64), name, url);
    }

    // Cardinal Components keys -> data attachments (see MythicAttachments)
    public static final EntityComponentKey<CarmotShield> CARMOT_SHIELD = MythicAttachments.CARMOT_SHIELD;
    public static final EntityComponentKey<CombustionCooldown> COMBUSTION_COOLDOWN = MythicAttachments.COMBUSTION_COOLDOWN;

    private static boolean initialized = false;

    public MythicMetals(IEventBus modBus, ModContainer container) {
        MythicAttachments.register(modBus);
        MythicResourceConditions.init(modBus);
        MythicOreBiomeModifier.register(modBus);

        // Registries are only writable during RegisterEvent: run the original Fabric initializer once, at the first
        // RegisterEvent (all built-in registries are unfrozen for the whole RegisterEvent phase), keeping its order.
        modBus.addListener(EventPriority.HIGHEST, RegisterEvent.class, event -> {
            if (!initialized) {
                initialized = true;
                onInitialize();
            }
        });

        modBus.<FMLCommonSetupEvent>addListener(event -> event.enqueueWork(this::registerDispenserBehaviour));

        // Fabric TradeOfferHelper.registerVillagerOffers(CLERIC, 5, ...)
        NeoForge.EVENT_BUS.<VillagerTradesEvent>addListener(event -> {
            if (event.getType() == VillagerProfession.CLERIC) {
                event.getTrades().get(5).add(new VillagerTrades.ItemsForEmeralds(MythicItems.Templates.AEGIS_SMITHING_TEMPLATE, 48, 1, 2, 30));
            }
        });
    }

    public void onInitialize() {
        TABBED_GROUP = createItemGroup();
        FieldRegistrationHandler.register(RegisterSounds.class, MOD_ID, false);
        FieldRegistrationHandler.processSimple(MythicItems.class, false);
        FieldRegistrationHandler.register(MythicItems.Mats.class, MOD_ID, false);
        FieldRegistrationHandler.register(MythicItems.Templates.class, MOD_ID, false);
        if (!net.neoforged.fml.loading.FMLLoader.isProduction()) {
            FieldRegistrationHandler.register(MythicItems.ParticleSticks.class, MOD_ID, false);
        }
        FieldRegistrationHandler.processSimple(MythicItems.Copper.class, false);
        FieldRegistrationHandler.register(MythicArmorMaterials.class, MOD_ID, false);
        FieldRegistrationHandler.processSimple(MythicTools.class, true);
        FieldRegistrationHandler.processSimple(MythicArmor.class, false);
        FieldRegistrationHandler.register(RegisterBlockEntityTypes.class, MOD_ID, false);
        MythicParticleSystem.init();
        MythicBlocks.init();
        MythicDataComponents.init();
        MythicPotions.init();
        BanglumNukeHandler.init();
        MythicOreFeatures.init();
        MythicCommands.init();
        MythicCommands.registerCommands();
        Abilities.init();
        RegisterPointOfInterests.init();
        MythicEntityAttributes.init();
        MythicEntities.init();
        TABBED_GROUP.initialize();
        // Fuel (Morkite 1200, Morkite block 12800): data/neoforge/data_maps/item/furnace_fuels.json
        // Resource conditions: registered as condition codecs in the constructor (MythicResourceConditions)
        RegisterLootConditions.init();
        MythicStatusEffects.init();
        MythicRecipeSerializers.init();
        FieldRegistrationHandler.processSimple(RegisterCriteria.class, false);
        BlockBreaker.initHammerTime();
        MythicLootOps.init();
        // Villager trade: VillagerTradesEvent (constructor). Dispenser behaviour: FMLCommonSetupEvent (needs bound holders).

        if (CONFIG.configVersion() < CONFIG_VERSION) {
            for (int i = 0; i < 5; i++) {
                LOGGER.warn("[Mythic Metals] Your config is outdated. Please update it manually in the file, or delete it so it can be re-generated.");
            }
        }

        if (net.neoforged.fml.ModList.get().isLoaded("harvest_scythes")) {
            LOGGER.info("[Mythic Metals] I see HarvestScythes. I'll take care of DH so you don't have to");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("enhancedcraft")) {
            LOGGER.info("[Mythic Metals] Oh EnhancedCraft? If you ever see Spxctre tell him I said hi!");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("origins")) {
            LOGGER.info("[Mythic Metals] Have fun using Origins!");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("spectrum")) {
            LOGGER.info("[Mythic Metals] Spectrum is loaded! Good luck on finding all of its secrets...");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("jello")) {
            LOGGER.info("[Mythic Metals] Is that Jello? Here comes the colors, weeeeeee!");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("terralith")) {
            LOGGER.info("[Mythic Metals] Terralith detected. Many ores can spawn in unexpected ways due to the new overworld. Modpack devs, take note of this");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("ftb-chunks-fabric") || net.neoforged.fml.ModList.get().isLoaded("ftb-chunks-neoforge")) {
            if (!net.neoforged.fml.ModList.get().isLoaded("ftb-xmod-compat-fabric")) {
                for (int i = 0; i < 3; i++) {
                    LOGGER.error("[Mythic Metals] FTB Chunks is loaded but FTB XMod Compat Fabric addon is not. This means claim protection will not work for some items!");
                }
            }
        }
        LOGGER.info("[Mythic Metals] Mythic Metals is now initialized.");
    }

    private void registerDispenserBehaviour() {
        DispenserBlock.registerBehavior(() -> MythicTools.STAR_PLATINUM_ARROW, new ProjectileDispenseBehavior(MythicTools.STAR_PLATINUM_ARROW));
        DispenserBlock.registerBehavior(() -> MythicTools.RUNITE_ARROW, new ProjectileDispenseBehavior(MythicTools.RUNITE_ARROW));
        DispenserBlock.registerBehavior(() -> MythicTools.TIPPED_RUNITE_ARROW, new ProjectileDispenseBehavior(MythicTools.TIPPED_RUNITE_ARROW));
    }
}
