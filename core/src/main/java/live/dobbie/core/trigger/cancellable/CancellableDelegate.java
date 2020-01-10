package live.dobbie.core.trigger.cancellable;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CancellableDelegate<C extends Cancellable> implements Cancellable {
    private final @NonNull C parent;
    private final @NonNull Supplier<CancellationHandler<C>> handler;

    @Override
    public void cancel(@NonNull Cancellation cancellation) {
        handler.get().cancel(parent, cancellation);
    }

    @Override
    public boolean isCancelled() {
        return handler.get().isCancelled(parent);
    }

    @Override
    public @NonNull Instant getTimestamp() {
        return parent.getTimestamp();
    }

    @Override
    public @NonNull String getSource() {
        return parent.getSource();
    }

    @Override
    public @NonNull String getName() {
        return parent.getName();
    }

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return parent.toLocString(loc);
    }
}
