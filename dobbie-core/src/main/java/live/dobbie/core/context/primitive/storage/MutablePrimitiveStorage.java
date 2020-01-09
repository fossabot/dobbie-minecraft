package live.dobbie.core.context.primitive.storage;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.NonNull;

public interface MutablePrimitiveStorage extends PrimitiveStorage {
    void setVariable(@NonNull Path key, @NonNull Primitive value);

    void removeVariable(@NonNull Path key);

    interface Factory extends PrimitiveStorage.Factory {
        MutablePrimitiveStorage create();
    }
}
