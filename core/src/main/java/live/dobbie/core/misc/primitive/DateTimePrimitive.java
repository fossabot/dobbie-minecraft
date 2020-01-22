package live.dobbie.core.misc.primitive;

import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Value
public class DateTimePrimitive implements Primitive, Comparable<DateTimePrimitive> {
    @NonNull Instant instant;

    @Override
    @NonNull
    public Instant getValue() {
        return instant;
    }

    @Override
    public int compareTo(@NotNull DateTimePrimitive o) {
        return instant.compareTo(o.instant);
    }
}
