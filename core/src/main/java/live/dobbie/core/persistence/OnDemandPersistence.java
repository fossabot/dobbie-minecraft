package live.dobbie.core.persistence;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.user.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OnDemandPersistence implements Persistence {
    private final @NonNull
    @Getter
    String name;
    private final @NonNull Persistence.Factory factory;
    private final @NonNull User user;
    private Persistence delegate;

    private Persistence delegate() {
        if (delegate == null) {
            delegate = factory.create(user);
        }
        return delegate;
    }

    @Override
    public Object get(@NonNull String key) throws StorageException {
        return delegate().get(key);
    }

    @Override
    public void set(@NonNull String key, Object object) throws StorageException {
        delegate().set(key, object);
    }

    @Override
    public void remove(@NonNull String key) throws StorageException {
        delegate().remove(key);
    }

    @Override
    public boolean contains(@NonNull String key) throws StorageException {
        return delegate().contains(key);
    }

    @Override
    public void cleanup() {
        if (delegate != null) {
            delegate.cleanup();
        }
    }

    @RequiredArgsConstructor
    public static class Factory implements Persistence.Factory {
        private final @NonNull String name;
        private final @NonNull Persistence.Factory delegateFactory;

        @NonNull
        @Override
        public Persistence create(@NonNull User user) {
            return new OnDemandPersistence(name, delegateFactory, user);
        }
    }
}
