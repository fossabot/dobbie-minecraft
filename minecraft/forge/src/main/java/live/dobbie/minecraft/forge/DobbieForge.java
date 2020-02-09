package live.dobbie.minecraft.forge;

import live.dobbie.core.plugin.DobbiePlugin;
import live.dobbie.core.plugin.ticker.ScheduledThreadPoolTicker;
import live.dobbie.core.plugin.ticker.Ticker;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.DobbieMinecraftBuilder;
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import live.dobbie.minecraft.util.logging.Slf4JLogger;
import lombok.NonNull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.Collections;

@Mod("dobbie")
public class DobbieForge {
    private static final String BRAND = "forge";

    private ILogger logger;
    private ForgeCompat forgeCompat;
    private MinecraftServer minecraftServer;
    private ScheduledThreadPoolTicker ticker;
    private DobbiePlugin dobbiePlugin;

    public DobbieForge() {
        Logging.setLoggerFactory(new Slf4JLogger.Factory());
        logger = Logging.getLogger(DobbieForge.class);
        forgeCompat = new ForgeCompat(() -> minecraftServer);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        logger.debug("Server starting");
        this.minecraftServer = event.getServer();
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
                () -> forgeCompat,
                ticker,
                Collections.emptyMap(),
                (cb, trigger) -> cb.set("forge", minecraftServer)
        );
        plugin.start();
        return plugin;
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        logger.debug("Server stopping");
        this.dobbiePlugin.cleanup();
        this.minecraftServer = null;
        this.dobbiePlugin = null;
        this.ticker = null;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity nativePlayer = extractNativePlayer(event);
        logger.debug("Player joined: " + nativePlayer);
        ticker.schedule(() -> dobbiePlugin.registerUser(new ForgeUser(forgeCompat, nativePlayer)));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayerEntity nativePlayer = extractNativePlayer(event);
        ticker.schedule(() -> dobbiePlugin.unregisterUser(new ForgeUser(forgeCompat, nativePlayer)));
    }

    @NonNull
    private static ServerPlayerEntity extractNativePlayer(@NonNull PlayerEvent playerEvent) {
        return (ServerPlayerEntity) playerEvent.getPlayer();
    }
}
