package live.dobbie.minecraft.bukkit.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuperBuilder
public class BukkitItemInfo extends MinecraftItemInfo implements BukkitItemStackConvertible {
    @Override
    public @NonNull ItemStack toItemStack(@NonNull MinecraftIdConverter converter) {
        String id = getId(), convertedId = converter.convertItemId(id);
        Material material = Material.matchMaterial(convertedId);
        if(material == null) {
            throw new IllegalArgumentException("could not find " + Material.class + " by name " + id + "(converted: " + convertedId + ")");
        }
        return new ItemStack(material, getCount());
    }

    public static ItemStack getItemStack(@NonNull MinecraftItemInfoBuilder itemInfoBuilder, @NonNull MinecraftIdConverter converter) {
        return getItemStack(itemInfoBuilder.build(), converter);
    }

    public static ItemStack getItemStack(MinecraftItemInfo itemInfo, @NonNull MinecraftIdConverter converter) {
        if(itemInfo == null) {
            return new ItemStack(Material.AIR);
        }
        if(itemInfo instanceof BukkitItemInfo) {
            return ((BukkitItemInfo) itemInfo).toItemStack(converter);
        }
        throw new IllegalArgumentException(MinecraftItemInfo.class + " must be created in " + BukkitItemInfoFactory.class);
    }
}
