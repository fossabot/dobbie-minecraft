package live.dobbie.minecraft.bukkit;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class BukkitScheduler implements Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(BukkitScheduler.class);

    private final @NonNull JavaPlugin plugin;

    @NonNull
    public org.bukkit.scheduler.BukkitScheduler getNativeScheduler() {
        return plugin.getServer().getScheduler();
    }

    @Override
    public void schedule(Runnable r) {
        getNativeScheduler().runTask(plugin, () -> {
            try {
                r.run();
            } catch(Exception e) {
                LOGGER.error("Scheduled task threw an error in Bukkit worker", e);
            }
        });
    }

    @Override
    public <V> V scheduleAndWait(Supplier<V> supplier) {
        Future<V> future = getNativeScheduler().callSyncMethod(plugin, supplier::get);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Scheduled task threw an error in Bukkit worker", e);
        }
    }
}
