package live.dobbie.minecraft.compat.world;

import lombok.NonNull;

public interface MinecraftWorldTable {
    @NonNull MinecraftWorldId overworld();
    @NonNull MinecraftWorldId theNether();
    @NonNull MinecraftWorldId theEnd();
    MinecraftWorldId byName(@NonNull String name);

    default MinecraftWorldId name(@NonNull String name) {
        return byName(name);
    }
}
