package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.bukkit.compat.world.BukkitWorld;
import live.dobbie.minecraft.bukkit.compat.world.BukkitWorldId;
import live.dobbie.minecraft.compat.MinecraftServer;
import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BukkitServer implements MinecraftServer, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(BukkitServer.class);

    private final @NonNull
    @Getter
    BukkitCompat instance;
    private final @NonNull Supplier<Server> serverSupplier;
    private final @NonNull
    @Delegate
    Scheduler scheduler;

    @Override
    public @NonNull Server getNativeServer() {
        return getNativeServerUnsafe();
    }

    @Override
    public BukkitPlayer getPlayer(@NonNull String name) {
        return scheduleAndWait(() -> getNativeServer().getOnlinePlayers().stream()
                .filter(p -> name.equals(p.getName()))
                .map(p -> new BukkitPlayer(this, p))
                .findAny()
                .orElse(null));
    }

    @Override
    public BukkitPlayer getPlayerByUUID(@NonNull UUID uuid) {
        return scheduleAndWait(() -> {
            Player player = getNativeServer().getPlayer(uuid);
            return player == null ? null : new BukkitPlayer(this, player);
        });
    }

    @Override
    public @NonNull List<BukkitPlayer> getOnlinePlayers() {
        return scheduleAndWait(() -> getNativeServer().getOnlinePlayers().stream()
                .map(player -> new BukkitPlayer(this, player))
                .collect(Collectors.toList()));
    }

    @Override
    public boolean isAvailable() {
        return getNativeServerUnsafe() != null;
    }

    @Override
    public BukkitWorld getWorld(@NonNull MinecraftWorldId worldId) {
        UUID uuid = BukkitWorldId.getUUID(worldId);
        return uuid == null? null : getWorldByUUID(uuid);
    }

    @Override
    public void broadcastMessage(@NonNull String message) {
        LOGGER.debug("Broadcasting server-wide message: \"" + message + "\"");
        scheduleAndWait(() -> getNativeServer().broadcastMessage(message));
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.debug("Dispatching command as console: \"" + command + "\"");
        scheduleAndWait(() -> getNativeServer().dispatchCommand(getNativeServer().getConsoleSender(), command));
    }

    public BukkitWorld getWorldByUUID(@NonNull UUID uuid) {
        return scheduleAndWait(() -> {
            World world = getNativeServer().getWorld(uuid);
            return world == null ? null : new BukkitWorld(this, world);
        });
    }

    public Server getNativeServerUnsafe() {
        return serverSupplier.get();
    }
}
