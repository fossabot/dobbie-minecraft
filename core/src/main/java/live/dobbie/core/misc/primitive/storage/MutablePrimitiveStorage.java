package live.dobbie.core.misc.primitive.storage;

import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.NonNull;

public interface MutablePrimitiveStorage extends PrimitiveStorage {
    void setVariable(@NonNull Path key, @NonNull Primitive value);

    void removeVariable(@NonNull Path key);

    interface Factory extends PrimitiveStorage.Factory {
        MutablePrimitiveStorage create();
    }
}
