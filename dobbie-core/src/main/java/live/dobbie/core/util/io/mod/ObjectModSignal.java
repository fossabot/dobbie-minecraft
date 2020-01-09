package live.dobbie.core.util.io.mod;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * ModSignal that delegates its comparision functionality to the passed objects
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ObjectModSignal implements ModSignal {
    @NonNull Object[] objects;

    public ObjectModSignal(@NonNull Object... objects) {
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectModSignal that = (ObjectModSignal) o;
        return Arrays.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objects);
    }
}
