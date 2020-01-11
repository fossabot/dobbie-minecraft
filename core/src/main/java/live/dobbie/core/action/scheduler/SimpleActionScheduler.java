package live.dobbie.core.action.scheduler;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionErrorHandler;
import live.dobbie.core.action.ActionExecutionException;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleActionScheduler implements ActionScheduler {
    private static final @NonNull ILogger LOGGER = Logging.getLogger(SimpleActionScheduler.class);

    private final @NonNull Scheduler scheduler;
    private final @NonNull ActionErrorHandler errorHandler;

    public SimpleActionScheduler(@NonNull Scheduler scheduler, @NonNull Loc loc) {
        this(scheduler, new ActionErrorHandler.NotifyingUser(loc));
    }

    @Override
    public void schedule(@NonNull Action action) {
        scheduler.schedule(() -> {
            try {
                action.execute();
            } catch (ActionExecutionException e) {
                LOGGER.error("Error executing scheduled action", e);
                errorHandler.reportError(action, e);
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
