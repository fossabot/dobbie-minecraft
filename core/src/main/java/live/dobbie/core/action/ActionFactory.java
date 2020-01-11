package live.dobbie.core.action;

import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ActionFactory {
    Action createAction(@NonNull Trigger trigger);

    @RequiredArgsConstructor
    abstract class Typed<T extends Trigger> implements ActionFactory {
        private final @NonNull Class<T> type;

        @Override
        public Action createAction(@NonNull Trigger trigger) {
            if (type.isAssignableFrom(trigger.getClass())) {
                return create((T) trigger);
            }
            return null;
        }

        protected abstract Action create(@NonNull T trigger);
    }

    interface Provider {
        @NonNull <T extends Trigger> ActionFactory findFactory(@NonNull T trigger);

        default <T extends Trigger> Action get(@NonNull T trigger) {
            return findFactory(trigger).createAction(trigger);
        }
    }
}
