package com.mythicmetals.component;

import com.mythicmetals.MythicMetals;
import com.mythicmetals.armor.CarmotShield;
import com.mythicmetals.entity.CombustionCooldown;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Cardinal Components entity components -> NeoForge data attachments.
 */
public final class MythicAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MythicMetals.MOD_ID);

    // CCA: registry.registerForPlayers(CARMOT_SHIELD, CarmotShield::new, RespawnCopyStrategy.INVENTORY)
    public static final EntityComponentKey<CarmotShield> CARMOT_SHIELD = new EntityComponentKey<>(ATTACHMENT_TYPES, "carmot_shield", holder -> new CarmotShield((Player) holder));
    // CCA: registry.registerFor(LivingEntity.class, COMBUSTION_COOLDOWN, CombustionCooldown::new)
    public static final EntityComponentKey<CombustionCooldown> COMBUSTION_COOLDOWN = new EntityComponentKey<>(ATTACHMENT_TYPES, "combustion_cooldown", holder -> new CombustionCooldown((LivingEntity) holder));

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        NeoForge.EVENT_BUS.addListener(MythicAttachments::onPlayerClone);
    }

    /**
     * NeoForge already copies serializable attachments when the player is cloned without dying (End exit),
     * which is CCA's lossless copy. CCA's {@code RespawnCopyStrategy.INVENTORY} additionally copies on death
     * when the inventory is kept (keepInventory gamerule, or the player is a spectator), replicated here.
     */
    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        var original = event.getOriginal();
        var player = event.getEntity();
        boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || player.isSpectator();
        if (!keepInventory) return;

        var provider = player.registryAccess();
        var tag = new net.minecraft.nbt.CompoundTag();
        CARMOT_SHIELD.get(original).writeToNbt(tag, provider);
        CARMOT_SHIELD.get(player).readFromNbt(tag, provider);
    }

    private MythicAttachments() {
    }
}
