package live.dobbie.minecraft.compat.inventory;

import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.ToIntFunction;

public interface MinecraftInventorySlot extends ToIntFunction<MinecraftInventory> {
    int UNKNOWN_SLOT = Integer.MIN_VALUE;

    int getSlotIdIn(@NonNull MinecraftInventory inventory);

    @Override
    default int applyAsInt(MinecraftInventory inventory) {
        return getSlotIdIn(inventory);
    }

    @RequiredArgsConstructor
    class PlayerInventorySlot implements MinecraftInventorySlot {
        private final @NonNull ToIntFunction<MinecraftPlayerInventory> slotFunction;

        @Override
        public int getSlotIdIn(@NonNull MinecraftInventory inventory) {
            if (!(inventory instanceof MinecraftPlayerInventory)) {
                return UNKNOWN_SLOT;
            }
            MinecraftPlayerInventory playerInventory = (MinecraftPlayerInventory) inventory;
            return slotFunction.applyAsInt(playerInventory);
        }
    }
}
