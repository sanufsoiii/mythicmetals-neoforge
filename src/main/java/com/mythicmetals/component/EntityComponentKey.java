package com.mythicmetals.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

/**
 * Thin replacement for Cardinal Components' {@code ComponentKey}: an entity {@link AttachmentType}
 * that is saved with the same NBT layout as the CCA component ({@link NbtComponent}) and synced
 * to clients like {@code AutoSyncedComponent} (initial sync on tracking + explicit {@link #sync}).
 */
public final class EntityComponentKey<T extends EntityComponentKey.NbtComponent> {

    public interface NbtComponent {
        void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup);

        void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup);
    }

    private final DeferredHolder<AttachmentType<?>, AttachmentType<T>> type;

    public EntityComponentKey(DeferredRegister<AttachmentType<?>> register, String name, Function<IAttachmentHolder, T> factory) {
        this.type = register.register(name, () -> AttachmentType.builder(factory::apply)
                .serialize(new IAttachmentSerializer<CompoundTag, T>() {
                    @Override
                    public T read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                        var component = factory.apply(holder);
                        component.readFromNbt(tag, provider);
                        return component;
                    }

                    @Override
                    public CompoundTag write(T component, HolderLookup.Provider provider) {
                        var tag = new CompoundTag();
                        component.writeToNbt(tag, provider);
                        return tag;
                    }
                })
                .sync(new AttachmentSyncHandler<T>() {
                    @Override
                    public void write(RegistryFriendlyByteBuf buf, T component, boolean initialSync) {
                        var tag = new CompoundTag();
                        component.writeToNbt(tag, buf.registryAccess());
                        buf.writeNbt(tag);
                    }

                    @Override
                    public T read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, T previousValue) {
                        var component = previousValue != null ? previousValue : factory.apply(holder);
                        var tag = buf.readNbt();
                        if (tag != null) component.readFromNbt(tag, buf.registryAccess());
                        return component;
                    }
                })
                .build());
    }

    public AttachmentType<T> type() {
        return this.type.get();
    }

    public T get(Entity entity) {
        return entity.getData(this.type);
    }

    /**
     * CCA {@code ComponentKey#sync}: push the current state to the clients tracking the entity (server side only).
     */
    public void sync(Entity entity) {
        if (!entity.level().isClientSide()) entity.syncData(this.type);
    }
}
