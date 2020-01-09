package live.dobbie.core.context.primitive;

import lombok.NonNull;
import lombok.Value;

@Value
public class NumberPrimitive implements Primitive {
    @NonNull Number number;

    @Override
    @NonNull
    public Number getValue() {
        return number;
    }
}
