package live.dobbie.core.context.primitive.converter;

import live.dobbie.core.context.primitive.Primitive;
import lombok.NonNull;


public interface PrimitiveConverterProvider {
    <V> PrimitiveConverter<V, ? extends Primitive> getConverter(@NonNull Class<V> key);

    @NonNull
    default <V> PrimitiveConverter<V, ? extends Primitive> requireConverter(@NonNull Class<V> key) {
        PrimitiveConverter<V, ? extends Primitive> parser = getConverter(key);
        if (parser == null) {
            throw new RuntimeException("could not find converter for " + key);
        }
        return parser;
    }

    static PrimitiveConverterProviderImpl.Builder builder() {
        return PrimitiveConverterProviderImpl.builder();
    }
}
