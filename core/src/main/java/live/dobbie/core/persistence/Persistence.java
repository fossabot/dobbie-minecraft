package live.dobbie.core.persistence;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface Persistence extends Cleanable {
    @NonNull String getName();

    Object get(@NonNull String key) throws StorageException;

    void set(@NonNull String key, Object object) throws StorageException;

    void remove(@NonNull String key) throws StorageException;

    boolean contains(@NonNull String key) throws StorageException;

    default boolean is(@NonNull String key, Object object) throws StorageException {
        if (object == null) {
            return !contains(key);
        } else {
            return object.equals(get(key));
        }
    }

    interface Factory<P extends Persistence> {
        @NonNull P create(@NonNull User user);

        interface Provider {
            @NonNull <P extends Persistence> Factory<P> getFactory(Class<P> key);
        }
    }
}
