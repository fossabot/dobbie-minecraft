package live.dobbie.core.service;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

public class SettingsBasedServiceRef<S extends Service, V extends ISettingsValue> extends AbstractServiceRef<S> {
    private static final ILogger LOGGER = Logging.getLogger(SettingsBasedServiceRef.class);

    private final @NonNull User user;
    private final @NonNull Class<V> settingsClass;
    private final @NonNull ServiceFactory<S, V> factory;
    private SettingsSubscription<V> subscription;

    public SettingsBasedServiceRef(@NonNull Class<V> settingsKey, @NonNull String name, @NonNull User user, @NonNull ISettings settings,
                                   @NonNull ServiceFactory<S, V> factory, @NonNull ServiceRefProvider provider) {
        super(name, provider);
        this.user = user;
        this.settingsClass = settingsKey;
        this.factory = factory;
        this.subscription = settings.registerListener(settingsKey, this::onSettingsChanged);
    }

    void onSettingsChanged(V newValue) {
        checkCleanedUp();
        cleanupService();
        S newService;
        ServiceUnavailableException exception = null;
        try {
            newService = factory.createService(user, newValue);
            Validate.notNull(newService, "createService returned null");
        } catch (RuntimeException e) {
            LOGGER.warning("Could not create " + settingsClass + " from " + newValue, e);
            newService = null;
            if (e instanceof ServiceUnavailableException) {
                exception = (ServiceUnavailableException) e;
            } else {
                exception = new ServiceUnavailableException("could not create " + settingsClass + " from " + newValue, e);
            }
        }
        fireUpdate(newService, exception);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (subscription != null) {
            subscription.cancelSubscription();
            subscription = null;
        }
    }

    public interface ServiceFactory<S extends Service, V extends ISettingsValue> {
        @NonNull S createService(@NonNull User user, V value) throws ServiceUnavailableException;

        @RequiredArgsConstructor
        abstract
        class Requiring<S extends Service, V extends ISettingsValue> implements ServiceFactory<S, V> {
            private final @NonNull Class<V> settingsKey;

            @NonNull
            @Override
            public final S createService(@NonNull User user, V value) throws ServiceUnavailableException {
                if (value == null) {
                    throw new ServiceUnavailableException(settingsKey + " is not provided");
                }
                return createServiceSafe(user, value);
            }

            protected abstract @NonNull S createServiceSafe(@NonNull User user, @NonNull V value) throws ServiceUnavailableException;
        }
    }

    @RequiredArgsConstructor
    public static class Factory<S extends Service, V extends ISettingsValue> implements ServiceRef.Factory<S> {
        private final @NonNull Class<V> settingsKey;
        private final @NonNull String name;
        private final @NonNull UserSettingsProvider settingsProvider;
        private final @NonNull ServiceFactory<S, V> factory;

        @Override
        public @NonNull SettingsBasedServiceRef<S, V> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user) {
            return new SettingsBasedServiceRef<>(settingsKey, name, user, settingsProvider.get(user), factory, provider);
        }
    }
}
