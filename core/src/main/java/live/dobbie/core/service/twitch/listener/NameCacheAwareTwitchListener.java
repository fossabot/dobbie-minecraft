package live.dobbie.core.service.twitch.listener;

import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import live.dobbie.core.service.twitch.NameCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;


@RequiredArgsConstructor
public class NameCacheAwareTwitchListener implements TwitchListener {
    private final @NonNull NameCache nameCache;
    private final @NonNull TwitchListener delegate;

    @Override
    public void onIRCMessage(@NonNull IRCMessageEvent event) {
        delegate.onIRCMessage(event);
    }

    @Override
    public void onMessage(@NonNull ChannelMessageEvent event) {
        delegate.onMessage(new ChannelMessageEvent(convertChannel(event.getChannel()), convertUser(event.getUser()), event.getMessage(), event.getPermissions()));
    }

    @Override
    public void onFollow(@NonNull FollowEvent event) {
        delegate.onFollow(new FollowEvent(convertChannel(event.getChannel()), convertUser(event.getUser())));
    }

    @Override
    public void onRaid(@NonNull RaidEvent event) {
        delegate.onRaid(new RaidEvent(convertChannel(event.getChannel()), convertUser(event.getRaider()), event.getViewers()));
    }

    @Override
    public void onCheer(@NonNull CheerEvent event) {
        delegate.onCheer(new CheerEvent(convertChannel(event.getChannel()), convertUser(event.getUser()), event.getMessage(), event.getBits()));
    }

    @Override
    public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
        delegate.onGiftSubscription(new GiftSubscriptionsEvent(convertChannel(event.getChannel()), convertUser(event.getUser()), event.getSubscriptionPlan(), event.getCount(), event.getTotalCount()));
    }

    @Override
    public void onSubscription(@NonNull SubscriptionEvent event) {
        delegate.onSubscription(new SubscriptionEvent(convertChannel(event.getChannel()), convertUser(event.getUser()), event.getSubscriptionPlan(), event.getMessage(), event.getMonths(), event.getGifted(), event.getGiftedBy(), event.getSubStreak()));
    }

    @Override
    public void onChannelPointsRedemption(@NonNull ChannelPointsRedemptionEvent event) {
        delegate.onChannelPointsRedemption(new ChannelPointsRedemptionEvent(convertUser(event.getUser()), convertChannel(event.getChannel()), event.getRedemption()));
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    private EventChannel convertChannel(@Nullable EventChannel channel) {
        if (channel == null) {
            return null;
        }
        if (channel.getName() == null) {
            channel = new EventChannel(channel.getId(), requestLogin(channel.getId()));
        }
        return channel;
    }

    private EventUser convertUser(@Nullable EventUser user) {
        if (user == null) {
            return null;
        }
        return new EventUser(user.getId(), requestDisplayName(user.getId(), user.getName()));
    }

    private String requestDisplayName(String userId, String username) {
        return nameCache.getDisplayNameOrLogin(userId, username);
    }

    private String requestLogin(String userId) {
        return nameCache.getLogin(userId);
    }
}
