package live.dobbie.minecraft.compat.entity;

import lombok.NonNull;

public interface MinecraftPlayer extends MinecraftEntityBase {
    @NonNull MinecraftPlayerInventory getInventory();

    @NonNull
    default Object getNativePlayer() {
        return getNativeEntity();
    }
}
