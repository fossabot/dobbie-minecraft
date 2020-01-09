package live.dobbie.minecraft.compat.converter;

import lombok.NonNull;

public interface MinecraftEnchantmentIdConverter {
    @NonNull String convertEnchantmentId(@NonNull String id);
}
