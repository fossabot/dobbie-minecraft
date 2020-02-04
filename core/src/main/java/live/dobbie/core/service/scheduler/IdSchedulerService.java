package live.dobbie.core.service.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.service.SingleServiceRef;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class IdSchedulerService implements IdTaskScheduler {
    public static final String NAME = "scheduler";
    private static final ILogger LOGGER = Logging.getLogger(IdSchedulerService.class);

    private final Hashtable<Object, Task> tasks = new Hashtable<>();
    private final ScheduledExecutorService service;

    IdSchedulerService(@NonNull ScheduledExecutorService service) {
        this.service = service;
    }

    public IdSchedulerService(@NonNull User user) {
        this.service = Executors.newScheduledThreadPool(0,
                new ThreadFactoryBuilder()
                        .setPriority(Thread.MIN_PRIORITY)
                        .setDaemon(false)
                        .setNameFormat("Dobbie" + IdSchedulerService.class.getSimpleName() + "-" + user.getName() + "-%d")
                        .setUncaughtExceptionHandler((t, e) ->
                                LOGGER.error("Uncaught exception in " + t.getName(), e)
                        )
                        .build()
        );
    }

    @Override
    public @NonNull IdScheduledTask scheduleAfter(@NonNull Object identifier, @NonNull Runnable r, long waitMillis) {
        checkNonNegative(waitMillis, "waitMillis");
        return schedule(identifier, t -> r.run(), t ->
                t.setFuture(service.schedule(t, waitMillis, TimeUnit.MILLISECONDS))
        );
    }

    @Override
    public @NonNull IdScheduledTask scheduleRepeating(@NonNull Object identifier, @NonNull Consumer<IdScheduledTask> task, long initialMillis, long waitMillis) {
        checkNonNegative(initialMillis, "initialMillis");
        checkNonNegative(waitMillis, "waitMillis");
        return schedule(identifier, task, t ->
                t.setFuture(service.scheduleWithFixedDelay(t, initialMillis, waitMillis, TimeUnit.MILLISECONDS))
        );
    }

    @Override
    public boolean cancel(@NonNull Object identifier) {
        synchronized (tasks) {
            Task task = tasks.get(identifier);
            if (task != null) {
                task.cancel();
                tasks.remove(identifier);
                return true;
            }
        }
        return false;
    }

    @Override
    public void cancelAll() {
        synchronized (tasks) {
            for (Map.Entry<Object, Task> objectTaskEntry : tasks.entrySet()) {
                objectTaskEntry.getValue().cancel();
            }
            tasks.clear();
        }
    }

    @Override
    public void cleanup() {
        cancelAll();
        service.shutdown();
    }

    private IdScheduledTask schedule(Object identifier, Consumer<IdScheduledTask> r, Consumer<Task> schedulerAction) {
        cancelIfFound(identifier);
        Task task = createTask(identifier, r);
        tasks.put(identifier, task);
        schedulerAction.accept(task);
        return task;
    }

    Task getTask(Object identifier) {
        return tasks.get(identifier);
    }

    Task createTask(Object identifier, Consumer<IdScheduledTask> r) {
        return new Task(identifier, r);
    }

    private void cancelIfFound(Object identifier) {
        Task task = tasks.remove(identifier);
        if (task != null) {
            task.cancel();
        }
    }

    private static void checkNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " < 0");
        }
    }

    @RequiredArgsConstructor
    @ToString(of = "identifier")
    static class Task implements Runnable, IdScheduledTask {
        private static final ILogger LOGGER = Logging.getLogger(Task.class);

        private final Object identifier;
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private final Consumer<IdScheduledTask> task;

        @Setter
        ScheduledFuture future;

        @Override
        public void run() {
            if (cancelled.get()) {
                return;
            }
            try {
                task.accept(this);
            } catch (RuntimeException e) {
                LOGGER.error("Error in task of id " + identifier, e);
                cancel();
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }

        @Override
        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                if (future != null) {
                    future.cancel(true);
                }
            }
        }
    }

    public static class RefFactory implements ServiceRef.Factory<IdTaskScheduler> {
        @Override
        public @NonNull ServiceRef<IdTaskScheduler> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user) {
            return new SingleServiceRef<>(NAME, new IdSchedulerService(user), provider);
        }
    }
}
