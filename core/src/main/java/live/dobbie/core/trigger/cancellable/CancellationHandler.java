package live.dobbie.core.trigger.cancellable;

import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface CancellationHandler<C extends Cancellable> extends Cleanable {
    void cancel(@NonNull C cancellable, @NonNull Cancellation cancellation);

    boolean isCancelled(@NonNull C cancellable);

    interface Factory<C extends Cancellable> {
        @NonNull CancellationHandler<C> create(@NonNull User user);
    }
}
