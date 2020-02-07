package live.dobbie.core.service.streamlabs.socket.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.service.streamlabs.socket.data.StreamLabsAuthor;
import live.dobbie.core.trigger.NamedTrigger;
import live.dobbie.core.trigger.cancellable.CancellableDelegate;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@ContextClass
@NamedTrigger("stream_labs_loyalty_store_redemption")
public class StreamLabsLoyaltyStoreRedemption implements StreamLabsTrigger {
    @NonNull User user;
    @NonNull Instant timestamp;
    @NonNull StreamLabsAuthor author;

    @ContextVar
    @NonNull String productName;

    String preferredDestination;

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("{author} redeemed {product_name} using StreamLabs")
                .set("product_name", productName)
                .copy(StreamLabsTrigger.super.toLocString(loc));
    }

    private final @NonNull CancellationHandler cancellationHandler;
    private final CancellableDelegate d =
            new CancellableDelegate(this, this::getCancellationHandler);

    @Override
    public void cancel(@NonNull Cancellation cancellation) {
        d.cancel(cancellation);
    }

    @Override
    public boolean isCancelled() {
        return d.isCancelled();
    }
}
