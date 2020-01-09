package live.dobbie.minecraft.compat;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface MinecraftServer<P extends MinecraftOnlinePlayer> extends UnreliableResource {
    @NonNull Object getNativeServer();

    P getPlayer(@NonNull String name);

    P getPlayerByUUID(@NonNull UUID uuid);

    @NonNull List<P> getOnlinePlayers();

    MinecraftWorld getWorldByName(@NonNull String name);

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

    default MinecraftWorld world(@NonNull String name) {
        return getWorldByName(name);
    }
}
