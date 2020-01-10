package live.dobbie.core.script.js.primitivestorage.adapter;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.context.primitive.storage.PrimitiveStorage;
import live.dobbie.core.path.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PSAdapter<S extends PrimitiveStorage> {
    protected final @NonNull S delegate;

    public Object get(@NonNull String key) {
        Primitive value = delegate.getVariable(toPath(key));
        return value == null ? null : value.getValue();
    }

    public boolean has(@NonNull String key) {
        return delegate.hasVariable(toPath(key));
    }

    protected Path toPath(String key) {
        return Path.parse(key);
    }
}
