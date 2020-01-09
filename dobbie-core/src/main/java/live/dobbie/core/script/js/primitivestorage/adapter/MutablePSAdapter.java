package live.dobbie.core.script.js.primitivestorage.adapter;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.context.primitive.storage.MutablePrimitiveStorage;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.NonNull;

public class MutablePSAdapter extends PSAdapter<MutablePrimitiveStorage> {
    private final @NonNull JSValueConverter valueConverter;

    public MutablePSAdapter(@NonNull MutablePrimitiveStorage delegate,
                            @NonNull JSValueConverter valueConverter) {
        super(delegate);
        this.valueConverter = valueConverter;
    }

    public void set(@NonNull String key, @NonNull Object value) {
        Path path = toPath(key);
        Object convertedObject = valueConverter.fromJs(value, Object.class);
        Primitive primitiveValue = Primitive.of(convertedObject);
        delegate.setVariable(path, primitiveValue);
    }

    public void remove(@NonNull String key) {
        delegate.removeVariable(toPath(key));
    }
}
