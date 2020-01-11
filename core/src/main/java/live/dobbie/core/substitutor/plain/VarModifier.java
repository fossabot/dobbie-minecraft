package live.dobbie.core.substitutor.plain;

import lombok.NonNull;
import lombok.Value;

@Value
public class VarModifier {
    @NonNull String name;
    @NonNull VarConverter varConverter;
}
