package live.dobbie.core.script.js.converter;

import live.dobbie.core.script.js.primitivestorage.accessor.AccessorAwareObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AccessorJSConverter implements TypedFromJSConverter<Object> {
    private final @NonNull FromJSConverter delegate;


    @Override
    public Object typedFromJs(Object object) {
        if (object instanceof AccessorAwareObject) {
            AccessorAwareObject accessor = (AccessorAwareObject) object;
            return accessor.unwrapValue();
        }
        return delegate.fromJs(object, Object.class);
    }
}
