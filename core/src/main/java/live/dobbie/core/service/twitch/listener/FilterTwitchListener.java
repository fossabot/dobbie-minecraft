package live.dobbie.core.service.twitch.listener;

import com.github.philippheuer.events4j.domain.Event;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.function.Predicate;

@RequiredArgsConstructor
@ToString
public class FilterTwitchListener implements TwitchListener {
    private final @NonNull Predicate<Event> filter;
    private final @NonNull TwitchListener delegate;

    @Override
    public void onIRCMessage(@NonNull IRCMessageEvent event) {
        if (filter.test(event)) {
            delegate.onIRCMessage(event);
        }
    }

    @Override
    public void onMessage(@NonNull ChannelMessageEvent event) {
        if (filter.test(event)) {
            delegate.onMessage(event);
        }
    }

    @Override
    public void onFollow(@NonNull FollowEvent event) {
        if (filter.test(event)) {
            delegate.onFollow(event);
        }
    }

    @Override
    public void onRaid(@NonNull RaidEvent event) {
        if (filter.test(event)) {
            delegate.onRaid(event);
        }
    }

    @Override
    public void onCheer(@NonNull CheerEvent event) {
        if (filter.test(event)) {
            delegate.onCheer(event);
        }
    }

    @Override
    public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
        if (filter.test(event)) {
            delegate.onGiftSubscription(event);
        }
    }

    @Override
    public void onSubscription(@NonNull SubscriptionEvent event) {
        if (filter.test(event)) {
            delegate.onSubscription(event);
        }
    }

    @Override
    public void onChannelPointsRedemption(@NonNull ChannelPointsRedemptionEvent event) {
        if (filter.test(event)) {
            delegate.onChannelPointsRedemption(event);
        }
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    @RequiredArgsConstructor
    @ToString
    public static class ChatRoomFilter implements Predicate<Event> {
        private final @NonNull String channelId;

        @Override
        public boolean test(Event event) {
            if (event instanceof ChannelPointsRedemptionEvent) {
                return channelId.equals(((ChannelPointsRedemptionEvent) event).getChannel().getId());
            }
            if (event instanceof IRCMessageEvent) {
                return channelId.equals(((IRCMessageEvent) event).getChannelId());
            }
            return event instanceof AbstractChannelEvent &&
                    ((AbstractChannelEvent) event).getChannel().getId().equals(channelId);
        }
    }
}
