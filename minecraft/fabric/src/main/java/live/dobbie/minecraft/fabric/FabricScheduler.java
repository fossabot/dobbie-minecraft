package live.dobbie.minecraft.fabric;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FabricScheduler implements Scheduler {
    private static final @NonNull ILogger LOGGER = Logging.getLogger(FabricScheduler.class);

    private final @NonNull Supplier<MinecraftServer> serverSupplier;

    @Override
    public void schedule(Runnable r) {
        MinecraftServer minecraftServer = requireServer();
        if (minecraftServer.isOnThread()) {
            executeRunnable(r); // we don't care about exceptions
        } else {
            minecraftServer.submit(() -> executeRunnable(r));
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
    public <V> @Nullable V scheduleAndWait(Supplier<V> task) {
        MinecraftServer minecraftServer = requireServer();
        if (minecraftServer.isOnThread()) {
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
