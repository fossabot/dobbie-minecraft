package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.entity.MinecraftPlayer;
import lombok.NonNull;

public interface MinecraftOnlinePlayer extends MinecraftPlayer {
    void sendMessage(@NonNull String message);

    void sendRawMessage(@NonNull String rawMessage);

    void executeCommand(@NonNull String command);
}
