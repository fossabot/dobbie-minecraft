package live.dobbie.core.service.twitch.data.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.service.twitch.TwitchChatClient;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.trigger.NamedTrigger;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.cancellable.CancellableDelegate;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@ContextClass
@NamedTrigger("twitch_raid")
public class TwitchRaid implements TwitchChatTrigger, Authored {
    @NonNull User user;
    @NonNull TwitchChatClient client;
    @NonNull TwitchChannel channel;
    @NonNull Instant timestamp;
    @NonNull TwitchUser twitchAuthor;
    int raiders;
    String preferredDestination;

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("{author} raided {twitch_channel} with {twitch_raiders_count}")
                .set("twitch_raiders_count", raiders)
                .copy(TwitchChatTrigger.super.toLocString(loc));
    }

    private final @NonNull CancellationHandler<TwitchChatTrigger> cancellationHandler;
    private final CancellableDelegate<TwitchChatTrigger> d =
            new CancellableDelegate<>(this, this::getCancellationHandler);

    @Override
    public void cancel(@NonNull Cancellation cancellation) {
        d.cancel(cancellation);
    }

    @Override
    public boolean isCancelled() {
        return d.isCancelled();
    }
}
