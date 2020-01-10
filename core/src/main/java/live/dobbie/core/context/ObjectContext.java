package live.dobbie.core.context;

import live.dobbie.core.context.primitive.storage.PrimitiveStorage;
import lombok.NonNull;

import java.util.Map;

public interface ObjectContext extends PrimitiveStorage {
    @NonNull Map<String, Object> getObjects();

    default <T> T getObject(@NonNull String key) {
        return (T) getObjects().get(key);
    }

    default @NonNull <T> T requireObject(@NonNull String key) {
        T object = getObject(key);
        if (object == null) {
            throw new IllegalArgumentException("object not found: " + key);
        }
        return object;
    }
}
