package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.bukkit.compat.BukkitCompat;
import live.dobbie.minecraft.bukkit.compat.BukkitServer;
import live.dobbie.minecraft.compat.inventory.MinecraftInventory;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
@ToString(of = {"entityRef", "uuid"})
public class BukkitEntity implements BukkitEntityBase {
    private final @NonNull BukkitCompat instance;
    private final @NonNull WeakReference<Entity> entityRef;
    private final @NonNull UUID uuid;

    @Override
    public @NonNull Entity getNativeEntity() {
        return getEntityUnsafe();
    }

    @Override
    public boolean isAvailable() {
        return getEntityUnsafe() != null;
    }

    @Override
    public @NonNull BukkitServer getServer() {
        return instance.getServer();
    }

    @Override
    public MinecraftInventory getInventory() {
        // TODO entity inventories not supported
        return null;
    }

    private Entity getEntityUnsafe() {
        return entityRef.get();
    }
}
