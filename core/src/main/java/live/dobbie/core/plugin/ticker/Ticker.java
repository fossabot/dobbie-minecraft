package live.dobbie.core.plugin.ticker;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public interface Ticker extends Scheduler, Cleanable {
    void start(@NonNull Runnable runnable);

    boolean awaitTermination(long time, TimeUnit timeUnit) throws InterruptedException;
}
