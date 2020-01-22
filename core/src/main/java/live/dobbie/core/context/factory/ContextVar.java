package live.dobbie.core.context.factory;

import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface ContextVar {
    @NonNull String[] path() default {};

    @NonNull Class<? extends PrimitiveConverter> parser() default PrimitiveConverter.class;

    boolean nullable() default false;
}
