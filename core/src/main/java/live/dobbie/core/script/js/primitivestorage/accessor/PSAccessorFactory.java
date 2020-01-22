package live.dobbie.core.script.js.primitivestorage.accessor;

import live.dobbie.core.misc.primitive.storage.MutablePrimitiveStorage;
import live.dobbie.core.misc.primitive.storage.PrimitiveStorage;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.converter.JSValueConverter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

@RequiredArgsConstructor
public class PSAccessorFactory {
    private final @NonNull
    @Getter
    PrimitiveStorage storage;
    private final @NonNull JSValueConverter valueConverter;
    private final @NonNull Scriptable scope;
    private final @NonNull Context context;

    public PSAccessor createAccessor(Path path) {
        if (storage instanceof MutablePrimitiveStorage) {
            return createForMutable((MutablePrimitiveStorage) storage, path);
        } else {
            return createForImmutable(storage, path);
        }
    }

    private ImmutablePSAccessor createForImmutable(PrimitiveStorage storage, Path path) {
        return new ImmutablePSAccessor(storage, path, valueConverter, scope, context);
    }

    private MutablePSAccessor createForMutable(MutablePrimitiveStorage storage, Path path) {
        return new MutablePSAccessor(storage, path, valueConverter, scope, context);
    }
}
