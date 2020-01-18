package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.SoundCategory;

public class BukkitSoundCategory extends MinecraftSoundCategory {
    private final @NonNull
    @Getter
    SoundCategory nativeCategory;

    public BukkitSoundCategory(@NonNull String name) {
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
        if (category instanceof BukkitSoundCategory) {
            return ((BukkitSoundCategory) category).getNativeCategory();
        } else {
            return toNative(category.getName());
        }
    }
}
