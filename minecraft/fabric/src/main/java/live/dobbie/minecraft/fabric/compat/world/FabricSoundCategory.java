package live.dobbie.minecraft.fabric.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.sound.SoundCategory;

public class FabricSoundCategory extends MinecraftSoundCategory {
    private final @NonNull
    @Getter
    SoundCategory nativeCategory;

    public FabricSoundCategory(@NonNull String name) {
        super(name);
        this.nativeCategory = toNative(name);
    }

    public static SoundCategory toNative(@NonNull String name) {
        try {
            return SoundCategory.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SoundCategory.MASTER;
        }
    }

    public static SoundCategory toNative(@NonNull MinecraftSoundCategory category) {
        if (category instanceof FabricSoundCategory) {
            return ((FabricSoundCategory) category).getNativeCategory();
        } else {
            return toNative(category.getName());
        }
    }
}
