package live.dobbie.core.misc.primitive;

import live.dobbie.core.util.Unboxing;
import lombok.NonNull;
import lombok.Value;

@Value
public class BoolPrimitive implements Primitive {
    public static BoolPrimitive
            FALSE = new BoolPrimitive(false),
            TRUE = new BoolPrimitive(true);

    @NonNull Boolean value;

    public boolean getBooleanValue() {
        return Unboxing.unbox(value);
    }
}
