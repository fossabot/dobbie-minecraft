package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlot;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.fabric.compat.FabricPlayer;
import live.dobbie.minecraft.fabric.compat.item.FabricItemInfo;
import live.dobbie.minecraft.fabric.compat.item.FabricNativeItemInfo;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "player")
@ToString(of = "player")
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
        return MinecraftInventorySlotTable.getModernSlotIdByName(name);
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
