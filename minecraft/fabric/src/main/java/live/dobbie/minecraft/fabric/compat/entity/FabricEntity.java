package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntity;
import live.dobbie.minecraft.compat.inventory.MinecraftInventory;
import live.dobbie.minecraft.fabric.compat.FabricCompat;
import live.dobbie.minecraft.fabric.compat.FabricServer;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
@ToString(of = {"uuid", "entityReference"})
public class FabricEntity implements FabricEntityBase, MinecraftEntity {
    private final @NonNull FabricCompat instance;
    private final @NonNull WeakReference<Entity> entityReference;
    private final @NonNull UUID uuid;

    public FabricEntity(@NonNull FabricCompat instance, @NonNull Entity entity) {
        this(instance, new WeakReference<>(entity), entity.getUuid());
    }

    @Override
    @NonNull
    public Entity getNativeEntity() {
        return entityReference.get();
    }

    @Override
    public @NonNull FabricServer getServer() {
        return instance.getServer();
    }

    @Override
    public void despawn() {
        if (isAvailable()) {
            getNativeEntity().remove();
        }
    }

    @Override
    public boolean isAvailable() {
        return entityReference.get() != null;
    }

    @Override
    public @NonNull String getUUID() {
        return uuid.toString();
    }

    // TODO entity inventories are not supported
    @Override
    public @Nullable MinecraftInventory getInventory() {
        return null;
    }
}
