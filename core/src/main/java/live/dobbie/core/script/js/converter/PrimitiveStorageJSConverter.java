package live.dobbie.core.script.js.converter;

import live.dobbie.core.context.primitive.storage.MutablePrimitiveStorage;
import live.dobbie.core.context.primitive.storage.PrimitiveStorage;
import live.dobbie.core.script.js.primitivestorage.adapter.ImmutablePSAdapter;
import live.dobbie.core.script.js.primitivestorage.adapter.MutablePSAdapter;
import live.dobbie.core.script.js.primitivestorage.adapter.PSAdapter;
import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class PrimitiveStorageJSConverter extends TypedJSValueConverter<PrimitiveStorage> {
    private final @NonNull JSValueConverter delegate;
    private final @NonNull JSValueConverter valueConverter;

    public PrimitiveStorageJSConverter(@NonNull JSValueConverter delegate, @NonNull JSValueConverter valueConverter) {
        super(PrimitiveStorage.class);
        this.delegate = delegate;
        this.valueConverter = valueConverter;
    }

    public PrimitiveStorageJSConverter(@NonNull JSValueConverter valueConverter) {
        this(valueConverter, valueConverter);
    }


    @Override
    public PrimitiveStorage typedFromJs(Object object) {
        return delegate.fromJs(object, PrimitiveStorage.class);
    }

    @Override
    public Object typedToJs(PrimitiveStorage storage, @NonNull Scriptable scope, @NonNull Context context) {
        if (storage != null) {
            PSAdapter adapter;
            if (storage instanceof MutablePrimitiveStorage) {
                adapter = new MutablePSAdapter((MutablePrimitiveStorage) storage, valueConverter);
            } else {
                adapter = new ImmutablePSAdapter(storage);
            }
            return delegate.toJs(adapter, scope, context);
        }
        return delegate.toJs(null, scope, context);
    }
}
