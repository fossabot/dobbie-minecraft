package live.dobbie.minecraft.bukkit.compat.entity;

import com.google.common.collect.Streams;
import live.dobbie.minecraft.bukkit.compat.BukkitPlayer;
import live.dobbie.minecraft.bukkit.compat.item.BukkitItemInfo;
import live.dobbie.minecraft.bukkit.compat.item.BukkitNativeItemInfo;
import live.dobbie.minecraft.compat.entity.MinecraftPlayerInventory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "player")
@ToString(of = "player")
public class BukkitPlayerInventory implements MinecraftPlayerInventory {
    private final @NonNull BukkitPlayer player;

    @Override
    public @NonNull BukkitPlayer getOwner() {
        return player;
    }

    @Override
    public @NonNull PlayerInventory getNativeInventory() {
        return player.getNativePlayer().getInventory();
    }

    @Override
    public int getSize() {
        return getNativeInventory().getSize();
    }

    @Override
    public boolean isEmpty() {
        return Streams.stream(getNativeInventory()).allMatch(is -> is == null || is.getType() == Material.AIR);
    }

    @Override
    public void addItem(@NonNull MinecraftItemInfo item) {
        getNativeInventory().addItem(toItemStack(item));
    }

    @Override
    public void removeItem(@NonNull MinecraftItemInfo item) {
        getNativeInventory().removeItem(toItemStack(item));
    }

    @Override
    public MinecraftItemInfo getItemAt(int slotId) {
        ItemStack itemStack = getNativeInventory().getItem(slotId);
        return BukkitNativeItemInfo.from(itemStack);
    }

    @Override
    public void setItemAt(int slotId, MinecraftItemInfo item) {
        ItemStack itemStack = toItemStack(item);
        getNativeInventory().setItem(slotId, itemStack);
    }

    @Override
    public int getSelectedSlot() {
        return getNativeInventory().getHeldItemSlot();
    }

    @Override
    public int getSlotIdByName(@NonNull String name) {
        return MinecraftInventorySlotTable.getModernSlotIdByName(name);
    }

    private ItemStack toItemStack(MinecraftItemInfo itemInfo) {
        return BukkitItemInfo.getItemStack(itemInfo, player.getServer().getInstance().getIdConverter());
    }
}
