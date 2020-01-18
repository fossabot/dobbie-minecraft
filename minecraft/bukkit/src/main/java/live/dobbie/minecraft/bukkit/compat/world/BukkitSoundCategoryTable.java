package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftSoundCategoryTable;
import lombok.NonNull;

public class BukkitSoundCategoryTable implements MinecraftSoundCategoryTable {
    @Override
    public @NonNull BukkitSoundCategory name(@NonNull String name) {
        return new BukkitSoundCategory(name);
    }
}
