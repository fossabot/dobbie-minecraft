package live.dobbie.core.storage;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface KeyValueStorage extends Cleanable {

    String get(@NonNull String key) throws StorageException;

    boolean exists(@NonNull String key) throws StorageException;

    void set(@NonNull String key, String value) throws StorageException;
}
