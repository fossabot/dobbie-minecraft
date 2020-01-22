package live.dobbie.core.misc.primitive;

import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
public class DateTimePrimitive implements Primitive {
    @NonNull Instant instant;

    @Override
    @NonNull
    public Instant getValue() {
        return instant;
    }
}
