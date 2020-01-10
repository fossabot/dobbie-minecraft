package live.dobbie.minecraft.compat.entity;

import live.dobbie.minecraft.compat.inventory.MinecraftInventory;
import lombok.NonNull;

public interface MinecraftPlayerInventory extends MinecraftInventory {
    @NonNull MinecraftPlayer getOwner();

    @NonNull
    default MinecraftPlayer getPlayer() {
        return getOwner();
    }

    int getSelectedSlot();

    int getSlotIdByName(@NonNull String name);
}
