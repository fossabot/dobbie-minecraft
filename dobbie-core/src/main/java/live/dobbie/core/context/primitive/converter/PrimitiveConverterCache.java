package live.dobbie.core.context.primitive.converter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.core.context.primitive.Primitive;
import lombok.NonNull;
import lombok.SneakyThrows;


public class PrimitiveConverterCache {
    private final LoadingCache<Class<? extends PrimitiveConverter>, PrimitiveConverter<?, ? extends Primitive>> cache =
            CacheBuilder.newBuilder().softValues().build(new CacheLoader<Class<? extends PrimitiveConverter>, PrimitiveConverter<?, ? extends Primitive>>() {
                @Override
                @NonNull
                public PrimitiveConverter<?, ? extends Primitive> load(@NonNull Class<? extends PrimitiveConverter> key) throws Exception {
                    return key.newInstance();
                }
            });

    @SneakyThrows
    @NonNull
    public PrimitiveConverter<?, ? extends Primitive> get(@NonNull Class<? extends PrimitiveConverter> preferredConverter) {
        return cache.get(preferredConverter);
    }
}
