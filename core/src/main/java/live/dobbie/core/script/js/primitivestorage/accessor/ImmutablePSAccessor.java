package live.dobbie.core.script.js.primitivestorage.accessor;

import live.dobbie.core.misc.primitive.storage.PrimitiveStorage;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class ImmutablePSAccessor extends PSAccessor<PrimitiveStorage> {
    public ImmutablePSAccessor(@NonNull PrimitiveStorage storage,
                               @NonNull Path path,
                               @NonNull JSValueConverter valueConverter,
                               @NonNull Scriptable scope,
                               @NonNull Context context) {
        super(storage, path, valueConverter, scope, context);
    }
}
