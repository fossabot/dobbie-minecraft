package live.dobbie.core.service.chargeback;

import live.dobbie.core.persistence.StorageException;
import live.dobbie.core.service.Service;
import live.dobbie.core.service.ServiceUnavailableException;
import live.dobbie.core.service.SettingsBasedServiceRef;
import live.dobbie.core.trigger.priced.Donated;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChargebackService implements Service {
    public static final String NAME = "chargebackService";
    private final @NonNull ChargebackStorage storage;

    public boolean isCommitted(@NonNull Donated trigger) throws StorageException {
        return storage.exists(ChargebackEntry.fromDonated(trigger));
    }

    public void commit(@NonNull Donated trigger) throws StorageException {
        if (!isCommitted(trigger)) {
            storage.add(ChargebackEntry.fromDonated(trigger));
        }
    }

    @Override
    public void cleanup() {
        storage.cleanup();
    }

    public static class Factory extends SettingsBasedServiceRef.ServiceFactory.Requiring<ChargebackService, ChargebackConfiguration> {
        private final @NonNull ChargebackStorage.Factory storageFactory;

        public Factory(@NonNull ChargebackStorage.Factory storageFactory) {
            super(ChargebackConfiguration.class);
            this.storageFactory = storageFactory;
        }

        @NonNull
        @Override
        protected ChargebackService createServiceSafe(@NonNull User user, @NonNull ChargebackConfiguration value) throws ServiceUnavailableException {
            if (!value.isEnabled()) {
                throw new ServiceUnavailableException("Chargeback service not enabled");
            }
            return new ChargebackService(storageFactory.create(user));
        }
    }

    public static class RefFactory extends SettingsBasedServiceRef.Factory<ChargebackService, ChargebackConfiguration> {
        public RefFactory(@NonNull UserSettingsProvider settingsProvider, @NonNull ChargebackStorage.Factory storageFactory) {
            super(ChargebackConfiguration.class, NAME, settingsProvider, new ChargebackService.Factory(storageFactory));
        }
    }
}
