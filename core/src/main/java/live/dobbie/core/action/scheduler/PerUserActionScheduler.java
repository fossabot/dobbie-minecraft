package live.dobbie.core.action.scheduler;

import live.dobbie.core.action.Action;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.cancellable.Cancellable;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationType;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@RequiredArgsConstructor
public class PerUserActionScheduler implements ActionScheduler {
    private static final ILogger LOGGER = Logging.getLogger(PerUserActionScheduler.class);
    private final Map<User, ExecutorService> serviceMap = new HashMap<>();

    private final @NonNull ActionScheduler fallbackIfNoUserProvidedExecutor;
    private final @NonNull Loc loc;
    private final @NonNull Function<User, ExecutorService> serviceFactory;

    public PerUserActionScheduler(@NonNull ActionScheduler fallbackIfNoUserProvidedExecutor, @NonNull Loc loc) {
        this(fallbackIfNoUserProvidedExecutor, loc, PerUserActionScheduler::createExecutor);
    }

    @Override
    public void schedule(@NonNull Action action) {
        User user = getUser(action);
        if (user == null) {
            fallbackIfNoUserProvidedExecutor.schedule(action);
        } else {
            ExecutorService executor = getExecutor(user);
            executor.submit(createTask(action));
        }
    }

    @Override
    public void registerUser(@NonNull User user) {
        if (serviceMap.containsKey(user)) {
            throw new RuntimeException("already registered: " + user);
        }
        ExecutorService service = serviceFactory.apply(user);
        if (service == null) {
            throw new NullPointerException("no service created for " + user);
        }
        serviceMap.put(user, service);
        fallbackIfNoUserProvidedExecutor.registerUser(user);
    }

    @Override
    public void unregisterUser(@NonNull User user) {
        ExecutorService removedService = serviceMap.remove(user);
        if (removedService == null) {
            throw new RuntimeException("not yet registered: " + user);
        }
        removedService.shutdown();
        fallbackIfNoUserProvidedExecutor.unregisterUser(user);
    }

    @Override
    public void cleanup() {
        serviceMap.values().forEach(ExecutorService::shutdown);
        serviceMap.clear();
        fallbackIfNoUserProvidedExecutor.cleanup();
    }

    @NonNull
    private Runnable createTask(Action action) {
        return () -> {
            try {
                action.execute();
            } catch (Exception e) {
                reportError(action, e);
            }
        };
    }

    private void reportError(Action action, Exception e) {
        LOGGER.error("Error executing action: " + action, e);
        UserRelatedTrigger trigger = (UserRelatedTrigger) action.getTrigger();
        LocString errorMessage = loc.withKey("An error occurred executing action: {message}")
                .set("message", e.toString());
        if (trigger instanceof Cancellable) {
            LOGGER.tracing("Cancelling");
            ((Cancellable) trigger).cancel(new Cancellation(CancellationType.FATAL, errorMessage));
        } else {
            LOGGER.tracing("Not cancellable");
            trigger.getUser().sendLocMessage(errorMessage);
        }
    }

    private ExecutorService getExecutor(User user) {
        ExecutorService service = serviceMap.get(user);
        if (service == null) {
            throw new RuntimeException("user not registered: " + user);
        }
        return service;
    }

    private static ExecutorService createExecutor(User user) {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName(PerUserActionScheduler.class.getSimpleName() + "-" + user.getName());
            return t;
        });
    }


    private static User getUser(Action action) {
        Trigger trigger = action.getTrigger();
        if (trigger instanceof UserRelatedTrigger) {
            return ((UserRelatedTrigger) trigger).getUser();
        }
        return null;
    }
}
