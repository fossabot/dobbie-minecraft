package live.dobbie.core.context.value;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;

public interface VarCondition {
    boolean isTrue(@NonNull Primitive value);
}
