package live.dobbie.core.misc.primitive;

import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class NumberPrimitive implements Primitive, Comparable<NumberPrimitive> {
    @NonNull Number number;

    @Override
    @NonNull
    public Number getValue() {
        return number;
    }

    @Override
    public int compareTo(@NotNull NumberPrimitive o) {
        return Float.compare(number.floatValue(), o.number.floatValue());
    }
}
