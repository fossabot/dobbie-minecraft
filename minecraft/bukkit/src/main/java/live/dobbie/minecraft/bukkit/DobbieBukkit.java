package live.dobbie.minecraft.bukkit;

import live.dobbie.core.plugin.DobbiePlugin;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.DobbieMinecraftBuilder;
import live.dobbie.minecraft.bukkit.compat.BukkitCompat;
import live.dobbie.minecraft.bukkit.compat.BukkitPlayer;
import live.dobbie.minecraft.bukkit.listener.BukkitListenerManager;
import live.dobbie.minecraft.bukkit.listener.BukkitListenerService;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class DobbieBukkit extends JavaPlugin implements Listener {
    private static final String BRAND = "bukkit";

    private @NonNull ILogger logger;

    @Override
    public void onLoad() {
        Logging.setLoggerFactory(new BukkitLogger.Factory(getLogger()));
        logger = Logging.getLogger(DobbieBukkit.class);
        bukkitCompat = new BukkitCompat(this::getServer, new BukkitScheduler(this));
    }

    private BukkitListenerManager listenerManager;
    private BukkitCompat bukkitCompat;
    private DobbiePlugin dobbiePlugin;

    @Override
    public void onEnable() {
        listenerManager = new BukkitListenerManager(this);
        initDobbiePlugin();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initDobbiePlugin() {
        this.dobbiePlugin = DobbieMinecraftBuilder.create(
                BRAND,
                getDataFolder(),
                () -> bukkitCompat,
                Collections.singletonMap(BukkitListenerService.class, new BukkitListenerService.RefFactory(listenerManager)),
                (cb, trigger) -> cb.set("bukkit", getServer())
        );
        this.dobbiePlugin.start();
    }

    @Override
    public void onDisable() {
        dobbiePlugin.cleanup();
        dobbiePlugin = null;
        listenerManager.cleanup();
        listenerManager = null;
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        dobbiePlugin.registerUser(new BukkitPlayer(bukkitCompat, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dobbiePlugin.unregisterUser(new BukkitPlayer(bukkitCompat, event.getPlayer()));
    }
}
