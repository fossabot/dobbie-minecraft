package live.dobbie.core.script.js.converter;

import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class DefaultValueConverter implements JSValueConverter {
    public static final DefaultValueConverter INSTANCE = new DefaultValueConverter();

    @Override
    public <V> V fromJs(Object object, Class<V> expectedType) {
        return (V) Context.jsToJava(object, expectedType);
    }

    @Override
    public Object toJs(Object object, @NonNull Scriptable scope, @NonNull Context context) {
        return Context.javaToJS(object, scope);
    }
}
