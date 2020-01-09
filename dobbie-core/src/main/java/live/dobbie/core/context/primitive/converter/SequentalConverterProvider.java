package live.dobbie.core.context.primitive.converter;

import live.dobbie.core.context.primitive.Primitive;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequentalConverterProvider implements PrimitiveConverterProvider {
    private final @NonNull List<PrimitiveConverterProvider> providerList;

    @Override
    public <V> PrimitiveConverter<V, ? extends Primitive> getConverter(@NonNull Class<V> key) {
        for (PrimitiveConverterProvider primitiveConverterProvider : providerList) {
            PrimitiveConverter<V, ? extends Primitive> parser = primitiveConverterProvider.getConverter(key);
            if (parser != null) {
                return parser;
            }
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<PrimitiveConverterProvider> providerList = new ArrayList<>();

        Builder() {
        }

        public Builder registerProvider(@NonNull PrimitiveConverterProvider provider) {
            providerList.add(provider);
            return this;
        }

        public SequentalConverterProvider build() {
            return new SequentalConverterProvider(new ArrayList<>(providerList));
        }
    }
}
