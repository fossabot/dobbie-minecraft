package live.dobbie.core.service.twitch.data.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextComplexVar;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.Price;
import live.dobbie.core.service.twitch.TwitchClient;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchChannelPointsReward;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.trigger.NamedTrigger;
import live.dobbie.core.trigger.cancellable.CancellableDelegate;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.Message;
import live.dobbie.core.trigger.messaged.Messaged;
import live.dobbie.core.trigger.priced.Priced;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@ContextClass
@NamedTrigger("twitch_channel_points_redemption")
public class TwitchChannelPointsRedemption implements TwitchTrigger, Messaged, Priced {
    @NonNull User user;
    @NonNull TwitchClient client;
    @NonNull TwitchChannel channel;
    @NonNull Instant timestamp;
    @NonNull TwitchUser twitchAuthor;

    @ContextComplexVar({
            @ContextVar(path = {"channel_points", "reward", "title"}, parser = TwitchChannelPointsReward.Title.class),
            @ContextVar(path = {"channel_points", "reward", "prompt"}, parser = TwitchChannelPointsReward.Prompt.class)
    })
    @NonNull TwitchChannelPointsReward reward;

    Message message;
    String preferredDestination;

    @Override
    public @NonNull Price getPrice() {
        return reward.getCost();
    }

    @NonNull
    @Override
    public LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("{author} redeemed {reward} for {price} from {twitch_channel} with message \"{message}\"")
                .set("reward", reward.getTitle())
                .copy(TwitchTrigger.super.toLocString(loc))
                .copy(Messaged.super.toLocString(loc))
                .copy(Priced.super.toLocString(loc));
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
