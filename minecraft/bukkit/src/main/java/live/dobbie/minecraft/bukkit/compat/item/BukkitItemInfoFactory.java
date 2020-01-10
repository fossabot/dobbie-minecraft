package live.dobbie.minecraft.bukkit.compat.item;

import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;

public class BukkitItemInfoFactory extends MinecraftItemInfoFactory {
    public BukkitItemInfo.BukkitItemInfoBuilder builder() {
        return BukkitItemInfo.builder();
    }
}
