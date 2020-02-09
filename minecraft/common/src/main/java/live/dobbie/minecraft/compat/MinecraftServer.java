package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.world.MinecraftWorld;
import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface MinecraftServer extends UnreliableResource {
    @NonNull Object getNativeServer();

    MinecraftOnlinePlayer getPlayer(@NonNull String name);

    MinecraftOnlinePlayer getPlayerByUUID(@NonNull UUID uuid);

    @NonNull List<? extends MinecraftOnlinePlayer> getOnlinePlayers();

    MinecraftWorld getWorld(@NonNull MinecraftWorldId worldId);

    void broadcastMessage(@NonNull String message);

    void executeCommand(@NonNull String command);

    default MinecraftOnlinePlayer getPlayerByUUID(@NonNull String uuid) {
        return getPlayerByUUID(UUID.fromString(uuid));
    }

    default MinecraftOnlinePlayer player(@NonNull String name) {
        return getPlayer(name);
    }

    default MinecraftOnlinePlayer playerUUID(@NonNull UUID uuid) {
        return getPlayerByUUID(uuid);
    }

    @NonNull
    default List<? extends MinecraftOnlinePlayer> onlinePlayers() {
        return getOnlinePlayers();
    }

    default MinecraftWorld world(@NonNull MinecraftWorldId worldId) {
        return getWorld(worldId);
    }
}
