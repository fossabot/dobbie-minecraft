package live.dobbie.core.trigger.cancellable;

import live.dobbie.core.loc.LocString;
import lombok.NonNull;
import lombok.Value;

@Value
public class Cancellation {
    @NonNull CancellationType type;
    @NonNull LocString reason;
}
