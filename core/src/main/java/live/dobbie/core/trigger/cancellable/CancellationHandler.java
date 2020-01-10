package live.dobbie.core.trigger.cancellable;

import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface CancellationHandler extends Cleanable {
    void cancel(@NonNull Cancellable cancellable, @NonNull Cancellation cancellation);

    interface Factory {
        @NonNull CancellationHandler create(@NonNull User user);
    }
}
