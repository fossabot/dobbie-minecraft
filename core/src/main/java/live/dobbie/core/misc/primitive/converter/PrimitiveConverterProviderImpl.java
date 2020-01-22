package live.dobbie.core.misc.primitive.converter;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PrimitiveConverterProviderImpl implements PrimitiveConverterProvider {
    private final @NonNull Map<Class, PrimitiveConverter> map;

    @Override
    public <V> PrimitiveConverter<V, ? extends Primitive> getConverter(@NonNull Class<V> key) {
        return map.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Class, PrimitiveConverter> map = new HashMap<>();

        Builder() {
        }

        public <IN, OUT extends Primitive> Builder registerConverter(
                @NonNull Class<IN> key,
                @NonNull PrimitiveConverter<IN, OUT> converter
        ) {
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("already registered: " + key);
            }
            map.put(key, converter);
            return this;
        }

        public Builder registerStandardConverters() {
            return registerConverter(Boolean.class, StandardConverters.BOOL_CONVERTER)
                    .registerConverter(boolean.class, StandardConverters.BOOL_CONVERTER)
                    .registerConverter(Number.class, StandardConverters.NUMBER_CONVERTER)
                    .registerConverter(int.class, StandardConverters.NumberConverter.instance())
                    .registerConverter(double.class, StandardConverters.NumberConverter.instance())
                    .registerConverter(long.class, StandardConverters.NumberConverter.instance())
                    .registerConverter(short.class, StandardConverters.NumberConverter.instance())
                    .registerConverter(byte.class, StandardConverters.NumberConverter.instance())
                    .registerConverter(String.class, StandardConverters.StringConverter.instance());
        }

        public PrimitiveConverterProvider build() {
            return new PrimitiveConverterProviderImpl(new HashMap<>(map));
        }
    }
}
