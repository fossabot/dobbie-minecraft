package live.dobbie.core.script.js.primitivestorage.accessor;

import live.dobbie.core.context.storage.MutablePrimitiveStorage;
import live.dobbie.core.context.storage.PrimitiveStorage;
import live.dobbie.core.misc.primitive.NullPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.util.Objects;

@RequiredArgsConstructor
public class AccessorAwareObject extends NativeObject {
    private final @NonNull PSAccessorFactory psAccessorFactory;
    private final @NonNull PrimitiveStorage storage;
    private final @NonNull JSValueConverter valueConverter;
    private final @NonNull Path path;

    public Object unwrapValue() {
        return Primitive.toObject(storage.getVariable(path));
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return valueConverter.toJs(unwrapValue(), this, Context.getCurrentContext());
    }

    @Override
    protected Object equivalentValues(Object value) {
        return Objects.equals(unwrapValue(), valueConverter.fromJs(value, Object.class));
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object object = super.get(name, start);
        if (object == NOT_FOUND) {
            AccessorAwareObject accessorAwareObject = new AccessorAwareObject(
                    psAccessorFactory,
                    storage,
                    valueConverter,
                    getPath(name)
            );
            super.put(name, start, accessorAwareObject);
            object = accessorAwareObject;
        }
        return object;
    }

    @Override
    public void put(String name, Scriptable start, Object jsValue) {
        Path path = getPath(name);
        setVariable(path, jsValue);
        defineAccessorAwareProperty(name, path);
    }

    private Path getPath(String name) {
        return this.path.merge(name);
    }

    private void defineAccessorAwareProperty(String name, Path path) {
        PSAccessor accessor = psAccessorFactory.createAccessor(path);
        defineProperty(name, accessor, accessor.getGetter(), accessor.getSetter(), 0);
    }

    private void defineAccessorAwareProperty(String name) {
        defineAccessorAwareProperty(name, getPath(name));
    }

    private void setVariable(Path currentPath, Object jsValue) {
        Primitive primitiveValue = valueConverter.fromJs(jsValue, Primitive.class);
        if (primitiveValue == null) {
            primitiveValue = NullPrimitive.INSTANCE;
        }
        requireMutability().setVariable(currentPath, primitiveValue);
    }

    private MutablePrimitiveStorage requireMutability() {
        if (storage instanceof MutablePrimitiveStorage) {
            return (MutablePrimitiveStorage) storage;
        }
        throw new RuntimeException("cannot set variable on an immutable primitive storage");
    }
}
