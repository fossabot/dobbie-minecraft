package live.dobbie.minecraft.forge;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ForgeScheduler implements Scheduler {
    private static final @NonNull ILogger LOGGER = Logging.getLogger(ForgeScheduler.class);

    private final @NonNull Supplier<MinecraftServer> serverSupplier;

    @Override
    public void schedule(Runnable r) {
        MinecraftServer minecraftServer = requireServer();
        if (minecraftServer.isOnExecutionThread()) {
            executeRunnable(r);
        } else {
            minecraftServer.deferTask(() -> executeRunnable(r));
        }
    }

    private static void executeRunnable(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            LOGGER.error("Scheduled task threw an error in server thread", e);
        }
    }

    @Override
    public <V> V scheduleAndWait(Supplier<V> task) {
        MinecraftServer minecraftServer = requireServer();
        if (minecraftServer.isOnExecutionThread()) {
            try {
                return task.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return CompletableFuture.supplyAsync(task, minecraftServer).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Scheduled task threw an error in server thread", e);
            }
        }
    }

    private MinecraftServer requireServer() {
        MinecraftServer minecraftServer = serverSupplier.get();
        if (minecraftServer == null) {
            throw new RuntimeException("server is not available");
        }
        return minecraftServer;
    }
}
