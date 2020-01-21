package live.dobbie.minecraft.compat.entity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MinecraftEntityDespawner {
    private static final ILogger LOGGER = Logging.getLogger(MinecraftEntityDespawner.class);

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public void queueDespawn(@NonNull MinecraftEntity entity, long ticks) {
        service.schedule(entity::despawn, ticksToMillis(ticks), TimeUnit.MILLISECONDS);
    }

    private static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

    private static ThreadFactory newThreadFactory() {
        return new ThreadFactoryBuilder()
                .setUncaughtExceptionHandler((t, e) -> LOGGER.error("Error in thread " + t.getName(), e))
                .setNameFormat(MinecraftEntityDespawner.class.getSimpleName() + "-%d")
                .setPriority(Thread.MIN_PRIORITY)
                .setDaemon(false)
                .build();
    }
}
