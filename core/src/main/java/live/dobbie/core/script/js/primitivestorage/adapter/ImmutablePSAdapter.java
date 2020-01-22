package live.dobbie.core.script.js.primitivestorage.adapter;

import live.dobbie.core.misc.primitive.storage.PrimitiveStorage;
import lombok.NonNull;

public class ImmutablePSAdapter extends PSAdapter<PrimitiveStorage> {
    public ImmutablePSAdapter(@NonNull PrimitiveStorage delegate) {
        super(delegate);
    }
}
