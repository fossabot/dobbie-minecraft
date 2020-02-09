package live.dobbie.minecraft.forge.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftSoundCategoryTable;
import lombok.NonNull;

public class ForgeSoundCategoryTable implements MinecraftSoundCategoryTable {
    @Override
    public @NonNull ForgeSoundCategory name(@NonNull String name) {
        return new ForgeSoundCategory(name);
    }
}
