package live.dobbie.core.context.value;

import live.dobbie.core.context.ObjectContext;
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
public class VarContextualCondition implements ContextualCondition {
    private final @NonNull Map<Path, VarCondition> conditions;

    @NonNull
    @Override
    public Boolean computeValue(@NonNull ObjectContext context) throws ComputationException {
        return conditions.entrySet().stream()
                .allMatch(entry ->
                        entry.getValue().isTrue(context.requireVariable(entry.getKey()))
                );
    }

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Builder {
        private final Map<Path, VarCondition> conditions = new HashMap<>();

        public Builder addCondition(@NonNull Path path, @NonNull VarCondition condition) {
            if (conditions.containsKey(path)) {
                throw new IllegalArgumentException();
            }
            conditions.put(path, condition);
            return this;
        }

        public VarContextualCondition build() {
            return new VarContextualCondition(new HashMap<>(conditions));
        }
    }
}
