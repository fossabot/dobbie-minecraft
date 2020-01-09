package live.dobbie.core.context.primitive.converter;

import live.dobbie.core.context.primitive.Primitive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class ReflectiveConverter<IN, OUT extends Primitive> implements PrimitiveConverter<Object, OUT> {
    private final PrimitiveConverter<IN, OUT> delegateConverter;
    private final Method method;

    @NonNull
    @Override
    public OUT parse(@NonNull Object object) {
        IN value;
        try {
            value = Validate.notNull((IN) method.invoke(object));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("could not get value from " + method + " using " + object, e);
        }
        return delegateConverter.parse(value);
    }
}
