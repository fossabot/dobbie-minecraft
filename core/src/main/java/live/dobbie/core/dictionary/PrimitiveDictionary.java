package live.dobbie.core.dictionary;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface PrimitiveDictionary extends Cleanable {

    @NonNull Primitive get(@NonNull String key) throws StorageException;

    boolean exists(@NonNull String key) throws StorageException;

    void set(@NonNull String key, @NonNull Primitive value) throws StorageException;
}
