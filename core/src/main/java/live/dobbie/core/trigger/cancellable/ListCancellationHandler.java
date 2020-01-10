package live.dobbie.core.trigger.cancellable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListCancellationHandler implements CancellationHandler {
    private final @NonNull List<CancellationHandler> handlers;

    @Override
    public void cancel(Cancellable cancellable, @NonNull Cancellation cancellation) {
        for (CancellationHandler handler : handlers) {
            handler.cancel(cancellable, cancellation);
        }
    }
}
