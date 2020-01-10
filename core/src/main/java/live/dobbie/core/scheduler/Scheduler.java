package live.dobbie.core.scheduler;


import java.util.function.Supplier;

public interface Scheduler {
    void schedule(Runnable r);


    <V> V scheduleAndWait(Supplier<V> supplier);

    default void scheduleAndWait(Runnable r) {
        scheduleAndWait(() -> {
            r.run();
            return null;
        });
    }
}
