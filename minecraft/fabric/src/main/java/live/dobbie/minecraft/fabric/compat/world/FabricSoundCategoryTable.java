package live.dobbie.minecraft.fabric.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftSoundCategoryTable;
import lombok.NonNull;

public class FabricSoundCategoryTable implements MinecraftSoundCategoryTable {
    @Override
    public @NonNull FabricSoundCategory name(@NonNull String name) {
        return new FabricSoundCategory(name);
    }
}
