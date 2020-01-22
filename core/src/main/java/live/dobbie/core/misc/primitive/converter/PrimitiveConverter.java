package live.dobbie.core.misc.primitive.converter;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;

public interface PrimitiveConverter<IN, OUT extends Primitive> {
    @NonNull OUT parse(@NonNull IN value);
}
