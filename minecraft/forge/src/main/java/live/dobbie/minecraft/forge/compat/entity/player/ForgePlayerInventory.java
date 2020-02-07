package live.dobbie.minecraft.forge.compat.entity.player;

import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlot;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.forge.compat.item.ForgeItemInfo;
import live.dobbie.minecraft.forge.compat.item.ForgeNativeItemInfo;
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
public class ForgePlayerInventory implements MinecraftPlayerInventory {
    public static final int ANY_SLOT = -1;

    private final @NonNull ForgePlayer player;

    @Override
    public @NonNull ForgePlayer getOwner() {
        return player;
    }

    @Override
    public PlayerInventory getNativeInventory() {
        return player.getNativePlayer().inventory;
    }

    @Override
    public int getSelectedSlot() {
        return getNativeInventory().currentItem;
    }

    @Override
    public int getSlotIdByName(@NonNull String name) {
        return MinecraftInventorySlotTable.getModernSlotIdByName(name);
    }

    @Override
    public int getSize() {
        return getNativeInventory().getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return getNativeInventory().isEmpty();
    }

    @Override
    public void addItem(@NonNull MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().placeItemBackInInventory(player.getNativePlayer().getServerWorld(), itemStack);
    }

    @Override
    public void removeItem(@NonNull MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().clearMatchingItems(is -> itemStack.getItem().equals(is.getItem()), item.getCount());
    }

    @Override
    public @Nullable ForgeNativeItemInfo getItemAt(int slotId) {
        ItemStack invStack = getNativeInventory().getStackInSlot(parseSlotId(slotId));
        return ForgeNativeItemInfo.from(invStack);
    }

    @Override
    public void setItemAt(int slotId, @Nullable MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().setInventorySlotContents(parseSlotId(slotId), itemStack);
    }

    private ItemStack toItemStack(@Nullable MinecraftItemInfo item) {
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return ForgeItemInfo.toItemStack(item, getOwner().getInstance().getIdConverter());
    }

    private static int parseSlotId(int slotId) {
        if (slotId == MinecraftInventorySlot.UNKNOWN_SLOT) {
            slotId = ANY_SLOT;
        }
        return slotId;
    }
}
