package live.dobbie.core.persistence;

import live.dobbie.core.service.Service;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.service.SingleServiceRef;
import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PersistenceService implements Service {
    public static final String NAME = "persistenceService";

    private final @NonNull List<Persistence> persistenceList;

    private final @NonNull ServiceRefProvider refProvider;
    private final Map<User, ServiceRef<PersistenceService>> discoveredServices = new HashMap<>();

    @NonNull
    public Persistence getStorage(@NonNull String name) {
        return persistenceList.stream().filter(persistence -> persistence.getName().equals(name)).findFirst().get();
    }

    @NonNull
    public Persistence storage(@NonNull String name) {
        return getStorage(name);
    }

    @NonNull
    public Persistence name(@NonNull String name) {
        return getStorage(name);
    }

    @NonNull
    public PersistenceService of(@NonNull User user) {
        ServiceRef<PersistenceService> ref = discoveredServices.get(user);
        if (ref == null) {
            ref = refProvider.createReference(PersistenceService.class, user);
            discoveredServices.put(user, ref);
        }
        return ref.getService();
    }

    @Override
    public void cleanup() {
        persistenceList.forEach(Cleanable::cleanup);
        persistenceList.clear();
        discoveredServices.values().forEach(Cleanable::cleanup);
        discoveredServices.clear();
    }

    @RequiredArgsConstructor
    public static class Factory {
        private final @NonNull List<Persistence.Factory> factoryList;

        public @NonNull PersistenceService create(@NonNull ServiceRefProvider provider, @NonNull User user) {
            return new PersistenceService(
                    factoryList.stream().map(factory -> factory.create(user)).collect(Collectors.toList()),
                    provider
            );
        }
    }


    @RequiredArgsConstructor
    public static class RefFactory implements ServiceRef.Factory<PersistenceService> {
        private final @NonNull Factory factory;

        public RefFactory(@NonNull List<Persistence.Factory> factoryList) {
            this(new Factory(factoryList));
        }

        @Override
        public @NonNull ServiceRef<PersistenceService> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user) {
            return new SingleServiceRef<>(NAME, factory.create(provider, user), provider);
        }
    }
}
