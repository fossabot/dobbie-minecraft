package live.dobbie.core.action.scheduler;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionExecutionException;
import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleActionScheduler implements ActionScheduler {
    private final @NonNull Scheduler scheduler;

    @Override
    public void schedule(@NonNull Action action) {
        scheduler.schedule(() -> {
            try {
                action.execute();
            } catch (ActionExecutionException e) {
                throw new RuntimeException("Action failed: " + action, e);
            }
        });
    }

    @Override
    public void registerUser(@NonNull User user) {
        // NO-OP
    }

    @Override
    public void unregisterUser(@NonNull User user) {
        // NO-OP
    }

    @Override
    public void cleanup() {
        // NO-OP
    }
}
