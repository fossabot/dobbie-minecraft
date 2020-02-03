package live.dobbie.core.service.twitch.data.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextComplexVar;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.service.twitch.TwitchClient;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchGame;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.trigger.NamedTrigger;
import live.dobbie.core.trigger.cancellable.CancellableDelegate;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;

@Value
@ContextClass
@NamedTrigger("twitch_channel_go_live")
public class TwitchChannelGoLive implements TwitchTrigger {
    @NonNull User user;
    @NonNull TwitchClient client;
    @NonNull TwitchChannel channel;
    @NonNull Instant timestamp;

    @ContextVar(path = {"twitch_stream", "start_time"})
    @NonNull Instant startTime;

    @ContextVar(path = {"twitch_stream", "is_recent"})
    boolean wentRecently;

    @ContextVar(path = {"twitch_stream", "title"})
    String title;

    @ContextComplexVar({
            @ContextVar(path = {"twitch_stream", "game", "id"}, parser = TwitchGame.Id.class),
            @ContextVar(path = {"twitch_stream", "game", "name"}, parser = TwitchGame.Name.class),
    })
    TwitchGame game;

    String preferredDestination;

    @Override
    public @NonNull TwitchUser getTwitchAuthor() {
        return getChannel();
    }

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("{twitch_channel} has gone Live!")
                .copy(TwitchTrigger.super.toLocString(loc));
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

    private static final long RECENT_THRESHOLD_SECONDS = 60 * 5;

    public static boolean isRecent(@NonNull Instant timestamp) {
        return Duration.between(Instant.now(), timestamp).getSeconds() < RECENT_THRESHOLD_SECONDS;
    }
}
