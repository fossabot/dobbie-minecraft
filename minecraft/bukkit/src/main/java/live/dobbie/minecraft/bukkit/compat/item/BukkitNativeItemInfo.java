package live.dobbie.minecraft.bukkit.compat.item;

import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BukkitNativeItemInfo extends BukkitItemInfo {

    BukkitNativeItemInfo(@NonNull ItemStack itemStack) {
        super(fromItem(itemStack));
    }

    @NonNull
    public static BukkitItemInfoBuilder fromItem(@NonNull ItemStack itemStack) {
        // TODO support more item properties
        BukkitItemInfoBuilder b = BukkitItemInfo.builder()
                .id(itemStack.getType().name().toLowerCase())
                .count(itemStack.getAmount());
        itemStack.getEnchantments().forEach((enchantment, level) ->
                b.enchantment(MinecraftItemEnchantment.builder()
                        .id(enchantment.getKey().toString())
                        .lvl(level.shortValue())
                )
        );
        return b;
    }

    @Nullable
    public static BukkitNativeItemInfo from(@Nullable ItemStack nativeItemStack) {
        if (nativeItemStack == null || nativeItemStack.getType() == Material.AIR) {
            return null;
        }
        return new BukkitNativeItemInfo(nativeItemStack);
    }
}
