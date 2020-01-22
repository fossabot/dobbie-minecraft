package live.dobbie.core.service.streamlabs.socket.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.Price;
import live.dobbie.core.service.streamlabs.socket.data.StreamLabsAuthor;
import live.dobbie.core.trigger.NamedTrigger;
import live.dobbie.core.trigger.cancellable.CancellableDelegate;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.Message;
import live.dobbie.core.trigger.messaged.Messaged;
import live.dobbie.core.trigger.priced.Donated;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@ContextClass
@NamedTrigger("stream_labs_donation")
public class StreamLabsDonation implements StreamLabsTrigger, Messaged, Donated {
    @NonNull User user;
    @NonNull Instant timestamp;
    @NonNull StreamLabsAuthor author;
    Message message;
    @NonNull Price donation;
    String preferredDestination;

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("{author} donated {donation} using StreamLabs{message_present, select, present { with message: \"{message}\"} other {}}")
                .copy(StreamLabsTrigger.super.toLocString(loc))
                .copy(Messaged.super.toLocString(loc))
                .copy(Donated.super.toLocString(loc));
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
