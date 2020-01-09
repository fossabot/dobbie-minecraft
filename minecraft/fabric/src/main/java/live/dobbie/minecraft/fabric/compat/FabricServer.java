package live.dobbie.minecraft.fabric.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftServer;
import live.dobbie.minecraft.compat.MinecraftWorld;
import live.dobbie.minecraft.fabric.FabricScheduler;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static live.dobbie.minecraft.fabric.FabricUtil.toNativeText;

public class FabricServer implements MinecraftServer<FabricPlayer>, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(FabricServer.class);

    private final @NonNull
    @Getter
    FabricCompat instance;
    private final @NonNull Supplier<net.minecraft.server.MinecraftServer> serverSupplier;
    private final @NonNull
    @Delegate
    FabricScheduler scheduler;

    public FabricServer(@NonNull FabricCompat instance, @NonNull Supplier<net.minecraft.server.MinecraftServer> serverSupplier) {
        this.instance = instance;
        this.serverSupplier = serverSupplier;
        this.scheduler = new FabricScheduler(serverSupplier);
    }

    @NonNull
    public net.minecraft.server.MinecraftServer getNativeServer() {
        return serverSupplier.get();
    }

    @Override
    public FabricPlayer getPlayer(@NonNull String name) {
        return scheduleAndWait(() -> {
            ServerPlayerEntity player = getNativeServer().getPlayerManager().getPlayer(name);
            return player == null ? null : new FabricPlayer(instance, player);
        });
    }

    @Override
    public FabricPlayer getPlayerByUUID(@NonNull UUID uuid) {
        return scheduleAndWait(() -> {
            ServerPlayerEntity player = getNativeServer().getPlayerManager().getPlayer(uuid);
            return player == null ? null : new FabricPlayer(instance, player);
        });
    }

    @Override
    public @NonNull List<FabricPlayer> getOnlinePlayers() {
        return scheduleAndWait(() ->
                getNativeServer().getPlayerManager().getPlayerList()
                        .stream()
                        .map(nativePlayer -> new FabricPlayer(instance, nativePlayer))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public MinecraftWorld getWorldByName(@NonNull String name) {
        return scheduleAndWait(() -> {
            Optional<DimensionType> dimensionTypeOptional = Registry.DIMENSION.getOrEmpty(Identifier.tryParse(name));
            if (dimensionTypeOptional.isPresent()) {
                return getWorldByType(dimensionTypeOptional.get());
            } else {
                LOGGER.warning("Could not find world by name \"" + name + "\"");
                return null;
            }
        });
    }

    public FabricWorld getWorldByType(@NonNull DimensionType type) {
        return scheduleAndWait(() -> {
            ServerWorld world = getNativeServer().getWorld(type);
            return world == null ? null : new FabricWorld(instance, world);
        });
    }

    @Override
    public void broadcastMessage(@NonNull String message) {
        schedule(() -> {
            getNativeServer().getPlayerManager().broadcastChatMessage(toNativeText(message), false);
        });
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.debug("Scheduling server command: " + command);
        schedule(() -> {
            LOGGER.info("Executing command as server: " + command);
            getNativeServer().getCommandManager().execute(getNativeServer().getCommandSource(), command);
        });
    }

    @Override
    public boolean isAvailable() {
        return serverSupplier.get() != null;
    }
}
