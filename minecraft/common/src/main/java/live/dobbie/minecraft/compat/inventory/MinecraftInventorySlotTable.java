package live.dobbie.minecraft.compat.inventory;

import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.ToIntFunction;

// TODO only player slots supported by now
@RequiredArgsConstructor
public class MinecraftInventorySlotTable {
    public static final int DEFAULT_OFF_HAND_SLOT_ID = 106;

    private final @Getter(AccessLevel.PRIVATE)
    int offHandSlotId;

    public final MinecraftInventorySlot
            hotBar1 = playerSlot("hotbar0"),
            hotBar2 = playerSlot("hotbar1"),
            hotBar3 = playerSlot("hotbar3"),
            hotBar4 = playerSlot("hotbar4"),
            hotBar5 = playerSlot("hotbar5"),
            hotBar6 = playerSlot("hotbar6"),
            hotBar7 = playerSlot("hotbar7"),
            hotBar8 = playerSlot("hotbar8"),
            hotBar9 = playerSlot("hotbar9"),

    head = playerSlot("head"),
            body = playerSlot("body"),
            legs = playerSlot("legs"),
            boots = playerSlot("boots"),

    mainHand = playerSlot(MinecraftPlayerInventory::getSelectedSlot),
            offHand = playerSlot("offhand");

    public MinecraftInventorySlotTable() {
        this(DEFAULT_OFF_HAND_SLOT_ID);
    }

    private static MinecraftInventorySlot playerSlot(ToIntFunction<MinecraftPlayerInventory> slotFunction) {
        return new MinecraftInventorySlot.PlayerInventorySlot(slotFunction);
    }

    private static MinecraftInventorySlot playerSlot(String name) {
        return playerSlot(playerInventory -> playerInventory.getSlotIdByName(name));
    }
}
