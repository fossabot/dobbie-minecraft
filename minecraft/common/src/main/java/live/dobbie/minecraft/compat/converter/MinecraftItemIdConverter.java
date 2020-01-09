package live.dobbie.minecraft.compat.converter;

import lombok.NonNull;

public interface MinecraftItemIdConverter {
    @NonNull String convertItemId(@NonNull String id);
}
