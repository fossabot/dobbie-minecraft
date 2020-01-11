package live.dobbie.core.settings;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.context.ISettingsContext;
import live.dobbie.core.settings.listener.ISettingsListener;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.settings.parser.ISettingsParser;
import live.dobbie.core.settings.source.ISettingsSource;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.*;

public class Settings implements ISettings {
    private static final ILogger LOGGER = Logging.getLogger(Settings.class);

    private final ISettingsSource source;
    private final @NonNull
    @Getter
    ISettingsParser.Provider parserProvider;
    private final @Getter(AccessLevel.PACKAGE)
    Map<Class, Value> valueMap = new HashMap<>();
    private final Context context = new Context();

    public <O extends ISettingsObject, S extends ISettingsSource<O>>
    Settings(S source, @NonNull ISettingsParser.Provider<O> parserProvider) {
        this.source = source;
        this.parserProvider = parserProvider;
    }

    @Override
    public <V extends ISettingsValue> V getValue(@NonNull Class<V> key) {
        return get(key).getValue();
    }

    @Override
    public <V extends ISettingsValue> SettingsSubscription<V> registerListener(@NonNull Class<V> key,
                                                                               @NonNull ISettingsListener<V> listener,
                                                                               boolean callListenerAfterwards) {
        return get(key).addListener(listener, callListenerAfterwards);
    }

    @Override
    public boolean refreshValues() {
        try {
            source.load();
        } catch (IOException | ParserException e) {
            LOGGER.error("Could not reload " + source, e);
        }
        boolean anyChanged = false;
        for (Value value : valueMap.values()) {
            anyChanged |= value.refreshValue();
        }
        return anyChanged;
    }

    private <V extends ISettingsValue> Value<V, ?, ?> findValue(Class<V> key) {
        return valueMap.get(key);
    }

    <V extends ISettingsValue> Value<V, ?, ?> get(Class<V> key) {
        Value<V, ?, ?> value = findValue(key);
        if (value == null) {
            value = createValue(key);
        }
        return value;
    }

    private <V extends ISettingsValue> Value<V, ?, ?> createValue(Class<V> key) {
        ISettingsParser<?, V> parser = parserProvider.findParser(key);
        Value<V, ?, ?> value = new Value<>(key, source, context, parser);
        value.refreshValue();
        valueMap.put(key, value);
        return value;
    }

    @Override
    public void cleanup() {
        valueMap.clear();
    }

    @RequiredArgsConstructor
    static class Value<V extends ISettingsValue, O extends ISettingsObject, S extends ISettingsSource<O>> {
        final @NonNull Class<V> key;
        final @NonNull S source;
        final @NonNull ISettingsContext context;
        final @NonNull ISettingsParser<O, V> parser;
        final @NonNull List<ISettingsListener<V>> listeners = new ArrayList<>();
        V value;

        V getValue() {
            return value;
        }

        boolean refreshValue() {
            V oldValue = value, newValue;
            try {
                newValue = parser.parse(source.getObject(), context);
            } catch (RuntimeException | ParserException rE) {
                LOGGER.warning("Cannot parse " + key, rE);
                newValue = null;
            }
            boolean valueChanged = !Objects.equals(oldValue, newValue);
            if (valueChanged) {
                LOGGER.debug("Value of " + key + " has changed: " + newValue);
                setNewValue(newValue);
            }
            return valueChanged;
        }

        void setNewValue(V newValue) {
            this.value = newValue;
            fireListeners();
        }

        void fireListeners() {
            final V value = this.value;
            this.listeners.forEach(listener -> {
                LOGGER.debug("Firing listener: " + listener);
                listener.onSettingsChanged(value);
            });
        }

        Subscription addListener(ISettingsListener<V> listener, boolean callListenerAfterwards) {
            LOGGER.debug("Adding listener: " + listener);
            Subscription subscription = new Subscription(listener);
            listeners.add(listener);
            if (callListenerAfterwards) {
                boolean valueChanged = refreshValue(); // ensure we have the latest value
                if (!valueChanged) {
                    LOGGER.debug("Exclusively firing listener: " + listener);
                    // we did not fire this listener yet
                    listener.onSettingsChanged(this.value);
                }
            }
            return subscription;
        }

        void removeListener(ISettingsListener<V> listener) {
            LOGGER.debug("Removing listener: " + listener);
            listeners.remove(listener);
        }

        @RequiredArgsConstructor
        class Subscription implements SettingsSubscription<V> {
            final @NonNull ISettingsListener<V> listener;


            @Override
            public V getValue() {
                return Value.this.getValue();
            }

            @Override
            public void cancelSubscription() {
                Value.this.removeListener(listener);
            }
        }
    }

    class Context implements ISettingsContext {
        @Override
        public <V extends ISettingsValue> V parse(@NonNull Class<V> key) throws ParserException {
            return getValue(key);
        }
    }
}
