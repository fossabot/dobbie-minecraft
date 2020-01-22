package live.dobbie.core.context;

import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;

import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimpleContext implements ObjectContext {
    @NonNull
    @Getter
    Map<String, Object> objects;
    @NonNull
    @Getter
    Map<Path, Primitive> variables;

    public SimpleContext() {
        this(Collections.emptyMap(), Collections.emptyMap());
    }

    public static SimpleContext merge(@NonNull Collection<ObjectContext> contexts) {
        Validate.noNullElements(contexts, "contexts contain null");
        HashMap<String, Object> objects = new HashMap<>();
        HashMap<Path, Primitive> variables = new HashMap<>();
        for (ObjectContext context : contexts) {
            objects.putAll(context.getObjects());
            variables.putAll(context.getVariables());
        }
        return new SimpleContext(objects, variables);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements ObjectContextBuilder {
        private final HashMap<String, Object> objects = new HashMap<>();
        private final HashMap<Path, Primitive> variables = new HashMap<>();
        private final Set<String> firstLevelVarNames = new HashSet<>();

        @Override
        public Builder set(@NonNull String key, @NonNull Object value) {
            if (objects.containsKey(key)) {
                throw new IllegalArgumentException("object already defined: " + key);
            }
            if (firstLevelVarNames.contains(key)) {
                throw new IllegalArgumentException("at least one variable already uses " + key + " as part of its path");
            }
            objects.put(key, value);
            return this;
        }

        @Override
        public Builder set(@NonNull Path path, @NonNull Primitive primitive) {
            if (variables.containsKey(path)) {
                throw new IllegalArgumentException("variable already defined: " + Path.toString(path));
            }
            path.ensureSizeAtLeast(1);
            String firstLevelName = path.at(0);
            if (objects.containsKey(firstLevelName)) {
                throw new IllegalArgumentException("object with this name already defined: " + firstLevelName +
                        " (path: " + Path.toString(path) + ")");
            }
            firstLevelVarNames.add(firstLevelName);
            variables.put(path, primitive);
            return this;
        }

        @Override
        public SimpleContext build() {
            return new SimpleContext(
                    Collections.unmodifiableMap(new HashMap<>(objects)),
                    Collections.unmodifiableMap(new HashMap<>(variables))
            );
        }

        private Builder() {
        }
    }
}
