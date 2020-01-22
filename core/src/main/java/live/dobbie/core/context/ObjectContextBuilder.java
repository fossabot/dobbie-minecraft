package live.dobbie.core.context;

import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import lombok.NonNull;

public interface ObjectContextBuilder {
    ObjectContextBuilder set(@NonNull String key, @NonNull Object value);

    ObjectContextBuilder set(@NonNull Path path, @NonNull Primitive primitive);

    ObjectContext build();
}
