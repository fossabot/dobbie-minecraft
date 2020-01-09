package live.dobbie.core.script.js.converter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

@RequiredArgsConstructor
public abstract class TypedJSValueConverter<V> implements JSValueConverter, TypedFromJSConverter<V>, TypedToJsConverter<V> {
    private final @NonNull
    @Getter(AccessLevel.PACKAGE)
    Class<V> cl;

    @Override

    public final <T> T fromJs(Object object, Class<T> expectedType) {
        requireConvertable(expectedType);
        return (T) this.typedFromJs(object);
    }

    @Override
    public Object toJs(Object object, @NonNull Scriptable scope, @NonNull Context context) {
        if (object == null) {
            throw new NullPointerException("null object passed into typed converter");
        }
        requireConvertable(object.getClass());
        return typedToJs((V) object, scope, context);
    }

    @Override

    public abstract V typedFromJs(Object object);

    @Override

    public abstract Object typedToJs(V object, @NonNull Scriptable scope, @NonNull Context context);

    boolean canConvert(Class<?> cl) {
        return this.cl.isAssignableFrom(cl);
    }

    private void requireConvertable(Class<?> cl) {
        if (!this.cl.isAssignableFrom(cl)) {
            throw new RuntimeException(this.cl + " is not assignable from " + cl);
        }
    }
}
