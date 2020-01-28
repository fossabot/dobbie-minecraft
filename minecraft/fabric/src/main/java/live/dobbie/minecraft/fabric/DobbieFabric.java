package live.dobbie.minecraft.fabric;

import live.dobbie.core.plugin.DobbiePlugin;
import live.dobbie.core.plugin.ticker.ScheduledThreadPoolTicker;
import live.dobbie.core.plugin.ticker.Ticker;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.DobbieMinecraftBuilder;
import live.dobbie.minecraft.fabric.compat.FabricCompat;
import live.dobbie.minecraft.fabric.compat.FabricPlayer;
import lombok.NonNull;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.function.Supplier;

public class DobbieFabric implements ModInitializer, ServerStartCallback, ServerStopCallback {
    private static final String BRAND = "fabric";

    private static DobbieFabric instance;

    private ILogger logger;
    private FabricCompat fabricCompat;
    private MinecraftServer minecraftServer;
    private ScheduledThreadPoolTicker ticker;
    private DobbiePlugin dobbiePlugin;

    @Override
    public void onInitialize() {
        DobbieFabric.instance = this;

        Logging.setLoggerFactory(new Slf4JLogger.Factory());
        logger = Logging.getLogger(DobbieFabric.class);

        Supplier<MinecraftServer> minecraftServerSupplier = () -> minecraftServer;
        fabricCompat = new FabricCompat(minecraftServerSupplier);

        ServerStartCallback.EVENT.register(this);
        ServerStopCallback.EVENT.register(this);
    }

    @Override
    public void onStartServer(MinecraftServer minecraftServer) {
        logger.debug("Server starting: " + minecraftServer);
        this.minecraftServer = minecraftServer;
        initDobbieInBackground();
    }

    private void initDobbieInBackground() {
        if (dobbiePlugin != null) {
            dobbiePlugin.cleanup();
        }
        if (ticker != null) {
            ticker.cleanup();
        }
        ticker = new ScheduledThreadPoolTicker();
        ticker.schedule(() -> dobbiePlugin = initDobbie(ticker));
    }

    private DobbiePlugin initDobbie(Ticker ticker) {
        DobbiePlugin plugin = DobbieMinecraftBuilder.create(
                BRAND,
                minecraftServer.getFile("config/Dobbie"),
                () -> fabricCompat,
                ticker,
                Collections.emptyMap(),
                (cb, trigger) -> cb.set("fabric", minecraftServer)
        );
        plugin.start();
        return plugin;
    }

    @Override
    public void onStopServer(MinecraftServer minecraftServer) {
        logger.debug("Server stopping: " + minecraftServer);
        this.dobbiePlugin.cleanup();
        this.minecraftServer = null;
        this.dobbiePlugin = null;
        this.ticker = null;
    }

    private void onPlayerJoined(@NonNull ServerPlayerEntity nativePlayer) {
        logger.debug("Player joined: " + nativePlayer);
        ticker.schedule(() -> dobbiePlugin.registerUser(new FabricPlayer(fabricCompat, nativePlayer)));
    }

    private void onPlayerQuit(@NonNull ServerPlayerEntity nativePlayer) {
        ticker.schedule(() -> dobbiePlugin.unregisterUser(new FabricPlayer(fabricCompat, nativePlayer)));
    }

    public static void playerJoined(ServerPlayerEntity player) {
        if (instance != null) {
            instance.onPlayerJoined(player);
        }
    }

    public static void playerQuit(ServerPlayerEntity player) {
        if (instance != null) {
            instance.onPlayerQuit(player);
        }
    }
}
