package live.dobbie.core.substitutor;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.context.primitive.storage.PrimitiveStorage;
import live.dobbie.core.path.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface VarProvider {
    String getVar(@NonNull String key);

    @NonNull
    default String requireVar(@NonNull String key) {
        String variable = getVar(key);
        if (variable == null) {
            throw new IllegalArgumentException("variable not found: " + key);
        }
        return variable;
    }

    @RequiredArgsConstructor
    class OfPrimitiveStorage implements VarProvider {
        private final @NonNull PrimitiveStorage primitiveStorage;

        @Override
        public String getVar(@NonNull String key) {
            return toString(primitiveStorage.getVariable(parseKey(key)));
        }

        @Override
        public @NonNull String requireVar(@NonNull String key) {
            return toString(primitiveStorage.requireVariable(parseKey(key)));
        }

        private static Path parseKey(@NonNull String key) {
            return Path.parse(key, ".");
        }

        private static String toString(Primitive value) {
            return Primitive.toString(value);
        }
    }
}
