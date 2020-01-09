package live.dobbie.minecraft.compat.item;

import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment.MinecraftItemEnchantmentBuilder;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo.MinecraftItemInfoBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftItemInfoFactory {
    public MinecraftItemInfoBuilder builder() {
        return MinecraftItemInfo.builder();
    }

    public MinecraftItemInfoBuilder id(@NonNull String id) {
        return builder().id(id);
    }

    public MinecraftItemEnchantmentBuilder enchantment() {
        return MinecraftItemEnchantment.builder();
    }

    public MinecraftItemInfoBuilder playerHeadItem(@NonNull String playerName) {
        return builder().id("player_head").skullOwner(playerName);
    }

    public MinecraftItemInfoBuilder playerHeadBlock(@NonNull String playerName) {
        return builder().id("player_wall_head").skullOwner(playerName);
    }
}
