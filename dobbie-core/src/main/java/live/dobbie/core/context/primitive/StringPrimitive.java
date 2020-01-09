package live.dobbie.core.context.primitive;

import lombok.NonNull;
import lombok.Value;

@Value
public class StringPrimitive implements Primitive {
    @NonNull String value;

    @Override
    @NonNull
    public Object getValue() {
        return value;
    }
}
