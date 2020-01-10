package live.dobbie.minecraft.compat.inventory;

import live.dobbie.minecraft.compat.entity.MinecraftEntityBase;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import lombok.NonNull;


public interface MinecraftInventory {
    @NonNull MinecraftEntityBase getOwner();

    @NonNull Object getNativeInventory();

    int getSize();

    boolean isEmpty();

    void addItem(@NonNull MinecraftItemInfo item);

    void removeItem(@NonNull MinecraftItemInfo item);

    MinecraftItemInfo getItemAt(int slotId);

    void setItemAt(int slotId, MinecraftItemInfo item);

    default void addItem(@NonNull MinecraftItemInfo.MinecraftItemInfoBuilder item) {
        addItem(item.build());
    }

    default void removeItem(@NonNull MinecraftItemInfo.MinecraftItemInfoBuilder item) {
        removeItem(item.build());
    }

    default void setItemAt(int slotId, MinecraftItemInfo.MinecraftItemInfoBuilder item) {
        setItemAt(slotId, item == null ? null : item.build());
    }

    default MinecraftItemInfo getItemAt(@NonNull MinecraftInventorySlot slot) {
        return getItemAt(slot.getSlotIdIn(this));
    }

    default void setItemAt(@NonNull MinecraftInventorySlot slot, MinecraftItemInfo item) {
        setItemAt(slot.getSlotIdIn(this), item);
    }

    default void setItemAt(@NonNull MinecraftInventorySlot slot, MinecraftItemInfo.MinecraftItemInfoBuilder item) {
        setItemAt(slot, item == null ? null : item.build());
    }
}
