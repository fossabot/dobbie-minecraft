package live.dobbie.core.plugin.ticker;

import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ScheduledThreadPoolTicker implements Ticker {
    private static final ILogger LOGGER = Logging.getLogger(ScheduledThreadPoolTicker.class);
    private static final String NAME = "Dobbie ticker";
    private static final long DEFAULT_DELAY_MS = 250;

    private final Impl impl = new Impl(NAME);
    private boolean hasTickTask;

    private final long delay;
    private final @NonNull TimeUnit unit;
    private final Consumer<Throwable> exceptionHandler;

    public ScheduledThreadPoolTicker() {
        this(DEFAULT_DELAY_MS, TimeUnit.MILLISECONDS, null);
    }

    @Override
    public void start(@NonNull Runnable runnable) {
        if (hasTickTask) {
            throw new IllegalStateException("already having tick task");
        }
        impl.scheduleWithFixedDelay(runnable, delay, delay, unit);
        hasTickTask = true;
    }

    @Override
    public void schedule(@NonNull Runnable runnable) {
        impl.submit(runnable);
    }

    @Override
    public <V> V scheduleAndWait(Supplier<V> task) {
        try {
            return impl.submit(task::get).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Scheduled task threw an exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
        impl.shutdown();
    }

    @Override
    public boolean awaitTermination(long time, TimeUnit timeUnit) throws InterruptedException {
        return impl.awaitTermination(time, timeUnit);
    }

    private class Impl extends ScheduledThreadPoolExecutor {
        public Impl(@NonNull String name) {
            super(1, r -> {
                Thread thread = new Thread(r);
                thread.setName(name);
                return thread;
            });
            setMaximumPoolSize(1); // do we even need this?
        }

        // https://stackoverflow.com/questions/2248131/handling-exceptions-from-java-executorservice-tasks
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null && r instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) r;
                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    t = ce;
                } catch (ExecutionException ee) {
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            if (t != null) {
                LOGGER.error("Uncaught ticker exception", t);
                if (exceptionHandler != null) {
                    exceptionHandler.accept(t);
                }
            }
        }
    }

}
