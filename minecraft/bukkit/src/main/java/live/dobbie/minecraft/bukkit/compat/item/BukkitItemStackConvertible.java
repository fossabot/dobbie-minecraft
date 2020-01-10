package live.dobbie.minecraft.bukkit.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public interface BukkitItemStackConvertible {
    @NonNull ItemStack toItemStack(@NonNull MinecraftIdConverter converter);
}
