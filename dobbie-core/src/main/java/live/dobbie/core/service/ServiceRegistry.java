package live.dobbie.core.service;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.list.ObjectContextInitializer;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserRegisterListener;
import live.dobbie.core.util.Cleanable;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ServiceRegistry implements UserRegisterListener, ServiceRefProvider, ObjectContextInitializer {
    private final HashMap<Class, Entries> storageMap;

    @Override
    public @NonNull <S extends Service> ServiceRef<S> createReference(@NonNull Class<S> serviceClass, @NonNull User user) {
        return byClass(serviceClass).byUser(user);
    }

    private <S extends Service> Entries<S> byClass(Class<S> serviceClass) {
        return Validate.notNull(storageMap.get(serviceClass), "service " + serviceClass + " not registered");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void initialize(@NonNull ObjectContextBuilder cb, @NonNull Trigger trigger) {
        if (!(trigger instanceof UserRelatedTrigger)) {
            return;
        }
        User user = ((UserRelatedTrigger) trigger).getUser();
        storageMap.values()
                .stream()
                .map(storage -> storage.byUser(user))
                .forEach(serviceRef -> {
                    if (serviceRef.isAvailable()) {
                        cb.set(serviceRef.getName(), serviceRef.getService());
                    }
                });
    }

    @Override
    public void registerUser(@NonNull User user) {
        storageMap.values().forEach(storage -> storage.registerUser(user));
    }

    @Override
    public void unregisterUser(@NonNull User user) {
        storageMap.values().forEach(storage -> storage.unregisterUser(user));
    }

    @Override
    public void cleanup() {
        storageMap.values().forEach(Entries::cleanup);
        storageMap.clear();
    }

    @RequiredArgsConstructor
    private static class Entries<S extends Service> implements UserRegisterListener {
        private final HashMap<User, ServiceRef<S>> refMap = new HashMap<>();
        private final @NonNull ServiceRef.Factory<S> factory;
        private final @NonNull ServiceRefProvider provider;

        ServiceRef<S> byUser(@NonNull User user) {
            if (!refMap.containsKey(user)) {
                throw new IllegalStateException("user was not registered: " + user);
            }
            return refMap.get(user);
        }

        @Override
        public void registerUser(@NonNull User user) {
            refMap.put(user, factory.createServiceRef(provider, user));
        }

        @Override
        public void unregisterUser(@NonNull User user) {
            ServiceRef<S> serviceRef = refMap.remove(user);
            serviceRef.cleanup();
        }

        @Override
        public void cleanup() {
            refMap.values().forEach(Cleanable::cleanup);
            refMap.clear();
        }
    }

    public static class Builder {
        private final HashMap<Class, ServiceRef.Factory> map = new HashMap<>();
        private final IntermediateRefProvider provider = new IntermediateRefProvider();

        public <S extends Service> Builder registerFactory(@NonNull Class<S> serviceClass, @NonNull ServiceRef.Factory<S> factory) {
            if (map.containsKey(serviceClass)) {
                throw new IllegalArgumentException("already registered: " + serviceClass);
            }
            map.put(serviceClass, factory);
            return this;
        }

        public ServiceRegistry build() {
            HashMap<Class, Entries> resultMap = new HashMap<>(map.size());
            map.forEach((serviceClass, factory) -> resultMap.put(serviceClass, new Entries(factory, provider)));
            ServiceRegistry registry = new ServiceRegistry(resultMap);
            provider.setProvider(registry);
            return registry;
        }
    }

    private static class IntermediateRefProvider implements ServiceRefProvider {
        private @Delegate
        @Setter(AccessLevel.PRIVATE)
        ServiceRefProvider provider;
    }
}
