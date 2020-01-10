package live.dobbie.core.trigger.cancellable;

import lombok.NonNull;

public interface CancellationHandler {
    void cancel(@NonNull Cancellable cancellable, @NonNull Cancellation cancellation);
}
