package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.world.MinecraftWorld;
import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface MinecraftServer<P extends MinecraftOnlinePlayer> extends UnreliableResource {
    @NonNull Object getNativeServer();

    P getPlayer(@NonNull String name);

    P getPlayerByUUID(@NonNull UUID uuid);

    @NonNull List<P> getOnlinePlayers();

    MinecraftWorld getWorld(@NonNull MinecraftWorldId worldId);

    void broadcastMessage(@NonNull String message);

    void executeCommand(@NonNull String command);

    default P getPlayerByUUID(@NonNull String uuid) {
        return getPlayerByUUID(UUID.fromString(uuid));
    }

    default P player(@NonNull String name) {
        return getPlayer(name);
    }

    default P playerUUID(@NonNull UUID uuid) {
        return getPlayerByUUID(uuid);
    }

    @NonNull
    default List<P> onlinePlayers() {
        return getOnlinePlayers();
    }

    default MinecraftWorld world(@NonNull MinecraftWorldId worldId) {
        return getWorld(worldId);
    }
}
