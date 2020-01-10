package live.dobbie.core.context.primitive.storage;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PrimitiveMap implements MutablePrimitiveStorage {
    private final @NonNull Map<Path, Primitive> map;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final @NonNull Map<Path, Primitive> unmodifiable = Collections.unmodifiableMap(map);

    public PrimitiveMap() {
        this(new HashMap<>());
    }

    @Override
    public void setVariable(@NonNull Path key, @NonNull Primitive value) {
        map.put(key, value);
    }

    @Override
    public void removeVariable(@NonNull Path key) {
        map.remove(key);
    }

    @Override
    public @NonNull Map<Path, Primitive> getVariables() {
        return getUnmodifiable();
    }

    public static class Factory implements MutablePrimitiveStorage.Factory {
        public static final Factory INSTANCE = new Factory();

        @Override
        public PrimitiveMap create() {
            return new PrimitiveMap();
        }
    }
}
