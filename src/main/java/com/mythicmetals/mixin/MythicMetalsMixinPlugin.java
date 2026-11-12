package com.mythicmetals.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class MythicMetalsMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch (mixinClassName) {
            case "com.mythicmetals.mixin.DamageOverTimePowerMixin" -> mythicmetals$isModLoaded("origins");
            case "com.mythicmetals.mixin.PiglinBrainMixin" ->
                !mythicmetals$isModLoaded("custom_piglin_bartering");
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    // ModList does not exist yet while mixins are being configured; LoadingModList does.
    private static boolean mythicmetals$isModLoaded(String modId) {
        return net.neoforged.fml.loading.LoadingModList.get().getModFileById(modId) != null;
    }
}
