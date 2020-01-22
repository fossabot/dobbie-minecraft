package live.dobbie.core.misc.primitive.converter;


import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Convertable {
    @NonNull Class<? extends PrimitiveConverter<?, ?>> value();
}
