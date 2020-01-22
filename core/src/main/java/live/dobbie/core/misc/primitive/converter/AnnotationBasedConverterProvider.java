package live.dobbie.core.misc.primitive.converter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;

public class AnnotationBasedConverterProvider implements PrimitiveConverterProvider {
    private static final Cache<Class<?>, PrimitiveConverter> converterCache = CacheBuilder.newBuilder().softValues().build();
    private static final Set<Class<?>> nullCache = new HashSet<>();

    @SneakyThrows
    @Override

    public <V> PrimitiveConverter<V, ? extends Primitive> getConverter(@NonNull Class<V> key) {
        if (nullCache.contains(key)) {
            return null;
        }
        PrimitiveConverter<V, ? extends Primitive> converter = converterCache.getIfPresent(key);
        if (converter != null) {
            return converter;
        }
        converter = findConverter(key);
        if (converter == null) {
            nullCache.add(key);
            return null;
        }
        return converter;
    }

    private <V> PrimitiveConverter<V, ? extends Primitive> findConverter(@NonNull Class<V> key) {
        ConvertableToString convertableToString = key.getAnnotation(ConvertableToString.class);
        if (convertableToString != null) {
            try {
                return (PrimitiveConverter<V, ? extends Primitive>) new ReflectiveConverter<>(
                        (PrimitiveConverter<V, ? extends Primitive>) StandardConverters.StringConverter.instance(),
                        key.getMethod(convertableToString.usingMethod())
                );
            } catch (NoSuchMethodException e) {
                throw new Error("method not found: " + convertableToString.usingMethod() + " in " + key);
            }
        }
        Convertable convertable = key.getAnnotation(Convertable.class);
        if (convertable != null) {
            try {
                return (PrimitiveConverter<V, ? extends Primitive>) convertable.value().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("could not create " + PrimitiveConverter.class + " for annotated class " + key, e);
            }
        }
        return null;
    }
}
