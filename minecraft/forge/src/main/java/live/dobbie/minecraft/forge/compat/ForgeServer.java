package live.dobbie.minecraft.forge.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftServer;
import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import live.dobbie.minecraft.forge.ForgeScheduler;
import live.dobbie.minecraft.forge.compat.entity.player.ForgePlayer;
import live.dobbie.minecraft.forge.compat.util.ForgeTextUtil;
import live.dobbie.minecraft.forge.compat.world.ForgeWorld;
import live.dobbie.minecraft.forge.compat.world.ForgeWorldId;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ForgeServer implements MinecraftServer<ForgePlayer>, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(ForgeServer.class);

    private final @NonNull
    @Getter
    ForgeCompat instance;
    private final @NonNull Supplier<net.minecraft.server.MinecraftServer> serverSupplier;
    private final @NonNull
    @Delegate
    ForgeScheduler scheduler;

    public ForgeServer(@NonNull ForgeCompat instance, @NonNull Supplier<net.minecraft.server.MinecraftServer> serverSupplier) {
        this.instance = instance;
        this.serverSupplier = serverSupplier;
        this.scheduler = new ForgeScheduler(serverSupplier);
    }

    @NonNull
    public net.minecraft.server.MinecraftServer getNativeServer() {
        return serverSupplier.get();
    }

    @Override
    public ForgePlayer getPlayer(@NonNull String name) {
        return scheduleAndWait(() -> {
            ServerPlayerEntity player = getNativeServer().getPlayerList().getPlayerByUsername(name);
            return player == null ? null : new ForgePlayer(instance, player);
        });
    }

    @Override
    public ForgePlayer getPlayerByUUID(@NonNull UUID uuid) {
        return scheduleAndWait(() -> {
            ServerPlayerEntity player = getNativeServer().getPlayerList().getPlayerByUUID(uuid);
            return player == null ? null : new ForgePlayer(instance, player);
        });
    }

    @Override
    public @NonNull List<ForgePlayer> getOnlinePlayers() {
        return scheduleAndWait(() ->
                getNativeServer().getPlayerList().getPlayers()
                        .stream()
                        .map(nativePlayer -> new ForgePlayer(instance, nativePlayer))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ForgeWorld getWorld(@NonNull MinecraftWorldId worldId) {
        return getWorldByType(ForgeWorldId.getDimensionType(worldId));
    }

    public ForgeWorld getWorldByType(@NonNull DimensionType type) {
        return scheduleAndWait(() -> {
            ServerWorld world = getNativeServer().getWorld(type);
            return world == null ? null : new ForgeWorld(instance, world);
        });
    }

    @Override
    public void broadcastMessage(@NonNull String message) {
        schedule(() -> {
            getNativeServer().getPlayerList().sendMessage(ForgeTextUtil.legacyToNative(message), false);
        });
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.debug("Scheduling server command: " + command);
        schedule(() -> {
            LOGGER.info("Executing command as server: " + command);
            getNativeServer().getCommandManager().handleCommand(getNativeServer().getCommandSource(), command);
        });
    }

    @Override
    public boolean isAvailable() {
        return serverSupplier.get() != null;
    }
}
