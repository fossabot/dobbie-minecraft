package live.dobbie.core.script.js.primitivestorage.accessor;

import live.dobbie.core.context.storage.PrimitiveStorage;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public abstract class PSAccessor<S extends PrimitiveStorage> {
    protected final @NonNull S storage;
    protected final @NonNull Path path;
    protected final @NonNull JSValueConverter valueConverter;
    protected final @NonNull Scriptable scope;
    protected final @NonNull Context context;

    public Object get(ScriptableObject object) {
        return valueConverter.toJs(storage.getVariable(path), scope, context);
    }

    public Method getGetter() {
        return GET;
    }

    public Method getSetter() {
        return null;
    }

    public static final Method GET;

    static {
        try {
            GET = PSAccessor.class.getMethod("get", ScriptableObject.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
