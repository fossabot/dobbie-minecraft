package live.dobbie.core.context.value;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.path.Path;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Value
public class VarCmpContextualCondition implements ContextualCondition {
    private final @NonNull Map<Path, Primitive> conditions;

    @NonNull
    @Override
    public Boolean computeValue(@NonNull ObjectContext context) throws ComputationException {
        return conditions.entrySet().stream()
                .allMatch(entry ->
                        context.requireVariable(entry.getKey()).equals(entry.getValue())
                );
    }

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Builder {
        private final Map<Path, Primitive> conditions = new HashMap<>();

        public Builder addCondition(@NonNull Path path, @NonNull Primitive primitive) {
            if (conditions.containsKey(path)) {
                throw new IllegalArgumentException();
            }
            conditions.put(path, primitive);
            return this;
        }

        public VarCmpContextualCondition build() {
            return new VarCmpContextualCondition(new HashMap<>(conditions));
        }
    }
}
