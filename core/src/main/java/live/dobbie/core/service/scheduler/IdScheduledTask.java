package live.dobbie.core.service.scheduler;

public interface IdScheduledTask {
    boolean isCancelled();

    void cancel();
}
