package live.dobbie.core.context.value;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.exception.ComputationException;
import lombok.NonNull;
import lombok.Value;

@Value
public class ConstContextualValue implements ContextualCondition {
    public static final ConstContextualValue ALWAYS_TRUE = new ConstContextualValue(Boolean.TRUE);

    private final @NonNull Boolean alwaysReturn;

    @NonNull
    @Override
    public Boolean computeValue(@NonNull ObjectContext context) throws ComputationException {
        return alwaysReturn;
    }
}
