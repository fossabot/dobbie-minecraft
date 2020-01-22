package live.dobbie.core.context.value;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import lombok.Value;

@Value
public class VarEqCondition implements VarCondition {
    @NonNull Primitive primitive;

    @Override
    public boolean isTrue(@NonNull Primitive value) {
        return primitive.equals(value);
    }
}
