package live.dobbie.core.script.js.primitivestorage.adapter;

import live.dobbie.core.context.primitive.storage.PrimitiveStorage;
import lombok.NonNull;

public class ImmutablePSAdapter extends PSAdapter<PrimitiveStorage> {
    public ImmutablePSAdapter(@NonNull PrimitiveStorage delegate) {
        super(delegate);
    }
}
