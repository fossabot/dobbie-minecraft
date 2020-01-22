package live.dobbie.core.context.value;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import lombok.Value;

@Value
public class VarCmpCondition implements VarCondition {
    @NonNull Primitive compareAgainst;
    @NonNull VarCmpCondType condition;

    public boolean isTrue(@NonNull Primitive value) {
        return condition.satisfies(value, compareAgainst);
    }
}
