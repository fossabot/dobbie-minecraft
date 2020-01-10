package live.dobbie.core.persistence;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PersistenceFactoryList implements Persistence.Factory.Provider {
    private final Map<Class<?>, Persistence.Factory<?>> factoryMap;

    @Override
    @NonNull
    public <P extends Persistence> Persistence.Factory<P> getFactory(Class<P> key) {
        Persistence.Factory<P> factory = (Persistence.Factory<P>) factoryMap.get(key);
        if (factory == null) {
            throw new IllegalArgumentException("could not find factory for " + key);
        }
        return factory;
    }

    public static class Builder {
        private final Map<Class<?>, Persistence.Factory<?>> factoryMap = new HashMap<>();

        public <P extends Persistence> Builder registerFactory(@NonNull Class<P> key, @NonNull Persistence.Factory<P> factory) {
            if (factoryMap.containsKey(key)) {
                throw new IllegalArgumentException("already registered: " + key);
            }
            factoryMap.put(key, factory);
            return this;
        }

        public PersistenceFactoryList build() {
            return new PersistenceFactoryList(new HashMap<>(factoryMap));
        }
    }
}
