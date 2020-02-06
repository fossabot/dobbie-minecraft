package live.dobbie.core.service.scheduler;

import live.dobbie.core.service.Service;
import lombok.NonNull;

import java.util.function.Consumer;

public interface IdTaskScheduler extends Service {
    @NonNull IdScheduledTask scheduleAfter(@NonNull Object identifier, @NonNull Runnable task, long waitMillis);

    @NonNull IdScheduledTask scheduleRepeating(@NonNull Object identifier, @NonNull Consumer<IdScheduledTask> task, long initialMillis, long waitMillis);

    boolean cancel(@NonNull Object identifier);

    void cancelAll();

    default @NonNull IdScheduledTask schedule(@NonNull Object identifier, @NonNull Runnable task) {
        return scheduleAfter(identifier, task, 0L);
    }

    default @NonNull IdScheduledTask schedule(@NonNull Runnable task) {
        return schedule(IdTask.unique(), task);
    }

    default @NonNull IdScheduledTask scheduleAfter(@NonNull Runnable task, long waitMillis) {
        return scheduleAfter(IdTask.unique(), task, waitMillis);
    }

    default @NonNull IdScheduledTask scheduleRepeating(@NonNull Object identifier, @NonNull Consumer<IdScheduledTask> task, long waitMillis) {
        return scheduleRepeating(identifier, task, 0L, waitMillis);
    }

    default @NonNull IdScheduledTask scheduleRepeating(@NonNull Consumer<IdScheduledTask> task, long initialMillis, long waitMillis) {
        return scheduleRepeating(IdTask.unique(), task, initialMillis, waitMillis);
    }

    default @NonNull IdScheduledTask scheduleRepeating(@NonNull Consumer<IdScheduledTask> task, long waitMillis) {
        return scheduleRepeating(task, 0, waitMillis);
    }
}
