package live.dobbie.core.action;

import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

public class ActionProvider implements Action.Factory.Provider {
    private static final ILogger LOGGER = Logging.getLogger(ActionProvider.class);

    private final Map<Class, Item> maps = new HashMap<>();

    public <T extends Trigger> void registerFactory(@NonNull Class<T> clazz, @NonNull Action.Factory<T> factory, boolean mayReplace) {
        LOGGER.tracing("Registering factory: " + clazz + ": " + factory);
        if (maps.containsKey(clazz)) {
            if (mayReplace) {
                LOGGER.debug("Replacing factory for class " + clazz + "; old: " + maps.get(clazz) + "; new: " + factory);
            } else {
                throw new IllegalArgumentException("already registered: " + clazz);
            }
        }
        maps.put(clazz, new Item<>(clazz, factory));
    }

    public <T extends Trigger> void registerFactory(@NonNull Class<T> clazz, @NonNull Action.Factory<T> factory) {
        registerFactory(clazz, factory, false);
    }

    @Override
    @NonNull
    public <T extends Trigger> Action.Factory<T> findFactory(@NonNull T trigger) {
        return find(trigger.getClass()).getFactory();
    }


    private <T extends Trigger> Item<? super T> get(@NonNull Class<?> clazz) {
        //LOGGER.tracing("Looking provider for " + clazz);
        if (!isTrigger(clazz)) {
            throw new IllegalArgumentException(clazz + " is not assignable from Trigger");
        }
        return maps.get(clazz);
    }

    @NonNull
    private Item find(@NonNull Class<?> clazz) {
        Item item = get(clazz);
        if (item == null) {
            if (clazz.equals(Trigger.class)) {
                throw new IllegalStateException("cannot find factory for super-class Trigger");
            }
            for (Class<?> anInterface : clazz.getInterfaces()) {
                if (!isTrigger(anInterface)) {
                    continue;
                }
                item = get(anInterface);
                return item == null ? find(anInterface) : item;
            }
            throw new IllegalStateException("cannot find factory for " + clazz);
        }
        return item;
    }

    private static boolean isTrigger(Class<?> clazz) {
        return Trigger.class.isAssignableFrom(clazz);
    }

    @Value
    private static class Item<T extends Trigger> implements Action.Factory<T> {
        private final @NonNull Class<T> clazz;
        private final @NonNull Action.Factory<T> factory;

        @Override
        public @NonNull Action createAction(@NonNull T trigger) {
            return factory.createAction(trigger);
        }
    }
}
