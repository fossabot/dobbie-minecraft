package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.entity.MinecraftPlayer;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import lombok.NonNull;

public interface MinecraftOnlinePlayer extends MinecraftPlayer {
    void sendMessage(@NonNull String message);

    void sendRawMessage(@NonNull String rawMessage);

    void executeCommand(@NonNull String command);

    void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory category, float volume, float pitch);
}
