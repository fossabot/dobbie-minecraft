package live.dobbie.core.script.js.converter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TypedValueConverter implements JSValueConverter {
    private final @NonNull Map<Class<?>, TypedFromJSConverter<?>> fromJsConverters;
    private final @NonNull Map<Class<?>, ToJSConverter> toJsConverters;
    private final @NonNull JSValueConverter fallbackConverter;

    @Override
    public <V> V fromJs(Object object, Class<V> expectedType) {
        if (object != null) {
            TypedFromJSConverter<V> converter = (TypedFromJSConverter<V>) findIn(fromJsConverters, expectedType, true);
            if (converter != null) {
                return converter.typedFromJs(object);
            }
        }
        return fallbackConverter.fromJs(object, expectedType);
    }

    @Override
    public Object toJs(Object object, @NonNull Scriptable scope, @NonNull Context context) {
        if (object != null) {
            ToJSConverter converter = findIn(toJsConverters, object.getClass(), true);
            if (converter != null) {
                return converter.toJs(object, scope, context);
            }
        }
        return fallbackConverter.toJs(object, scope, context);
    }

    private static <C> C findIn(Map<Class<?>, C> converterMap, Class<?> type, boolean mayBeParent) {
        C converter = converterMap.get(type);
        if (converter != null) {
            return converter;
        }
        if (!mayBeParent) {
            return null;
        }
        for (Class<?> s : ClassUtils.getAllSuperclasses(type)) {
            converter = converterMap.get(s);
            if (converter != null) {
                return converter;
            }
        }
        for (Class<?> i : ClassUtils.getAllInterfaces(type)) {
            converter = converterMap.get(i);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Class<?>, TypedFromJSConverter<?>> fromJsConverters = new HashMap<>();
        private final Map<Class<?>, ToJSConverter> toJsConverters = new HashMap<>();
        private JSValueConverter fallbackConverter;

        public <V> Builder registerFromConverter(@NonNull Class<V> expectedType, @NonNull TypedFromJSConverter<V> converter) {
            fromJsConverters.put(expectedType, converter);
            return this;
        }

        public Builder registerToConverter(@NonNull Class<?> expectedType, @NonNull ToJSConverter converter) {
            toJsConverters.put(expectedType, converter);
            return this;
        }

        public <V> Builder registerConverter(@NonNull TypedJSValueConverter<V> converter) {
            return registerFromConverter(converter.getCl(), converter)
                    .registerToConverter(converter.getCl(), converter);
        }

        public Builder setFallbackConverter(@NonNull JSValueConverter fallbackConverter) {
            this.fallbackConverter = fallbackConverter;
            return this;
        }

        public TypedValueConverter build() {
            Validate.notNull(fallbackConverter, "fallbackConverter");
            return new TypedValueConverter(
                    new HashMap<>(fromJsConverters),
                    new HashMap<>(toJsConverters),
                    fallbackConverter
            );
        }
    }
}
