package live.dobbie.minecraft.compat.world;

import lombok.NonNull;

public interface MinecraftSoundCategoryTable {
    @NonNull MinecraftSoundCategory name(@NonNull String name);

    default @NonNull MinecraftSoundCategory master() {
        return name("master");
    }

    default @NonNull MinecraftSoundCategory music() {
        return name("music");
    }

    default @NonNull MinecraftSoundCategory records() {
        return name("records");
    }

    default @NonNull MinecraftSoundCategory weather() {
        return name("weather");
    }

    default @NonNull MinecraftSoundCategory blocks() {
        return name("blocks");
    }

    default @NonNull MinecraftSoundCategory hostile() {
        return name("hostile");
    }

    default @NonNull MinecraftSoundCategory neutral() {
        return name("neutral");
    }

    default @NonNull MinecraftSoundCategory players() {
        return name("players");
    }

    default @NonNull MinecraftSoundCategory ambient() {
        return name("ambient");
    }

    default @NonNull MinecraftSoundCategory voice() {
        return name("voice");
    }
}
