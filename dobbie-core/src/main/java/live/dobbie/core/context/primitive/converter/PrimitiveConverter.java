package live.dobbie.core.context.primitive.converter;

import live.dobbie.core.context.primitive.Primitive;
import lombok.NonNull;

public interface PrimitiveConverter<IN, OUT extends Primitive> {
    @NonNull OUT parse(@NonNull IN value);
}
