package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlot;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.fabric.compat.FabricPlayer;
import live.dobbie.minecraft.fabric.compat.item.FabricItemInfo;
import live.dobbie.minecraft.fabric.compat.item.FabricNativeItemInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class FabricPlayerInventory implements MinecraftPlayerInventory {
    public static final int ANY_SLOT = -1;

    private final @NonNull FabricPlayer player;

    @Override
    public @NonNull FabricPlayer getOwner() {
        return player;
    }

    @Override
    public PlayerInventory getNativeInventory() {
        return player.getNativePlayer().inventory;
    }

    @Override
    public int getSelectedSlot() {
        return getNativeInventory().selectedSlot;
    }

    @Override
    public int getSlotIdByName(@NonNull String name) {
        switch (name) {
            case "hotbar1":
                return 0;
            case "hotbar2":
                return 1;
            case "hotbar3":
                return 2;
            case "hotbar4":
                return 3;
            case "hotbar5":
                return 4;
            case "hotbar6":
                return 5;
            case "hotbar7":
                return 6;
            case "hotbar8":
                return 7;
            case "hotbar9":
                return 8;
            case "boots":
                return 36;
            case "legs":
                return 37;
            case "body":
                return 38;
            case "head":
                return 39;
            case "offhand":
                return 40;
        }
        return MinecraftInventorySlot.UNKNOWN_SLOT;
    }

    @Override
    public int getSize() {
        return getNativeInventory().getInvSize();
    }

    @Override
    public boolean isEmpty() {
        return getNativeInventory().isInvEmpty();
    }

    @Override
    public void addItem(@NonNull MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().offerOrDrop(player.getNativePlayer().getServerWorld(), itemStack);
    }

    @Override
    public void removeItem(@NonNull MinecraftItemInfo item) {
        ItemStack itemStack = FabricItemInfo.toItemStack(item, getOwner().getInstance().getIdConverter());
        getNativeInventory().method_7369(is -> itemStack.getItem().equals(is.getItem()), item.getCount());
    }

    @Override
    public @Nullable FabricNativeItemInfo getItemAt(int slotId) {
        ItemStack invStack = getNativeInventory().getInvStack(parseSlotId(slotId));
        return FabricNativeItemInfo.from(invStack);
    }

    @Override
    public void setItemAt(int slotId, @Nullable MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().insertStack(parseSlotId(slotId), itemStack);
    }

    private ItemStack toItemStack(@Nullable MinecraftItemInfo item) {
        return FabricItemInfo.toItemStack(item, getOwner().getInstance().getIdConverter());
    }

    private static int parseSlotId(int slotId) {
        if (slotId == MinecraftInventorySlot.UNKNOWN_SLOT) {
            slotId = ANY_SLOT;
        }
        return slotId;
    }
}
