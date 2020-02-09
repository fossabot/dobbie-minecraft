package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntity;
import live.dobbie.minecraft.compat.inventory.MinecraftInventory;
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import live.dobbie.minecraft.forge.compat.ForgeServer;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.entity.Entity;

import java.lang.ref.SoftReference;
import java.util.UUID;

@EqualsAndHashCode(of = "uuid")
public class ForgeEntity implements ForgeEntityBase, MinecraftEntity {
    private final @NonNull ForgeCompat instance;
    private final @NonNull SoftReference<Entity> entityRef;
    private final @NonNull UUID uuid;

    public ForgeEntity(@NonNull ForgeCompat instance, @NonNull Entity entity) {
        this.instance = instance;
        this.entityRef = new SoftReference<>(entity);
        this.uuid = entity.getUniqueID();
    }

    @Override
    public @NonNull Entity getNativeEntity() {
        return getRawNativeEntity();
    }

    private Entity getRawNativeEntity() {
        return entityRef.get();
    }

    @Override
    public @NonNull ForgeServer getServer() {
        return instance.getServer();
    }

    @Override
    public @NonNull String getUUID() {
        return uuid.toString();
    }

    @Override
    public MinecraftInventory getInventory() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return getServer().isAvailable() && getRawNativeEntity() != null;
    }

    @Override
    public void despawn() {
        getNativeEntity().remove();
    }
}
