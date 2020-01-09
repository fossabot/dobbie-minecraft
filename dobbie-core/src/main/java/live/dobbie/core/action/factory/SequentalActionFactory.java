package live.dobbie.core.action.factory;

import live.dobbie.core.action.Action;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class SequentalActionFactory implements Action.Factory<Trigger> {
    private static final ILogger LOGGER = Logging.getLogger(SequentalActionFactory.class);

    private final @NonNull Collection<Action.Factory<Trigger>> factories;

    @Override

    public Action<Trigger> createAction(@NonNull Trigger trigger) {
        LOGGER.tracing("Looking for factory for trigger: " + trigger);
        for (Action.Factory<Trigger> factory : factories) {
            Action<Trigger> action = factory.createAction(trigger);
            if (action == null) {
                LOGGER.tracing(factory + "returned nothing");
                continue;
            }
            LOGGER.tracing("Found: " + factory);
            LOGGER.tracing("Created action: " + action);
            return action;
        }
        LOGGER.tracing("No factory found for this trigger");
        return null;
    }
}
