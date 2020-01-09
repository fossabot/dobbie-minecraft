package live.dobbie.minecraft.compat.converter;

import lombok.NonNull;

public interface MinecraftEntityNameConverter {
    @NonNull String convertEntityName(@NonNull String entityName);
}
