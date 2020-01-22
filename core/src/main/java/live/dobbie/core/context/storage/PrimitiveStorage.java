package live.dobbie.core.context.storage;

import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Map;

public interface PrimitiveStorage {
    @NonNull Map<Path, Primitive> getVariables();

    default Primitive getVariable(@NonNull Path path) {
        return getVariables().get(path);
    }

    default @NonNull Primitive requireVariable(@NonNull Path path) throws IllegalArgumentException {
        Primitive variable = getVariable(path);
        if (variable == null) {
            throw new IllegalArgumentException("variable not found: " + Path.toString(path));
        }
        return variable;
    }

    default boolean hasVariable(@NonNull Path path) {
        return getVariables().containsKey(path);
    }

    @RequiredArgsConstructor
    class Delegated implements PrimitiveStorage {
        private final @NonNull
        @Delegate
        PrimitiveStorage delegate;
    }

    interface Factory {
        PrimitiveStorage create();
    }
}
