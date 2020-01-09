package live.dobbie.core.script.js.primitivestorage.accessor;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.context.primitive.storage.MutablePrimitiveStorage;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Method;

public class MutablePSAccessor extends PSAccessor<MutablePrimitiveStorage> {

    public MutablePSAccessor(@NonNull MutablePrimitiveStorage storage,
                             @NonNull Path path,
                             @NonNull JSValueConverter valueConverter,
                             @NonNull Scriptable scope,
                             @NonNull Context context) {
        super(storage, path, valueConverter, scope, context);
    }

    public void set(ScriptableObject object, Object jsValue) {
        Object value = valueConverter.fromJs(jsValue, Object.class);
        storage.setVariable(path, Primitive.of(value));
    }

    @Override
    public Method getSetter() {
        return SET;
    }

    public static final Method SET;

    static {
        try {
            SET = MutablePSAccessor.class.getMethod("set", ScriptableObject.class, Object.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
