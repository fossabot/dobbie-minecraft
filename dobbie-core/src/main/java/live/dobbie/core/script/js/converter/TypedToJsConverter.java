package live.dobbie.core.script.js.converter;

import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface TypedToJsConverter<V> {
    Object typedToJs(V object, @NonNull Scriptable scope, @NonNull Context context);
}
