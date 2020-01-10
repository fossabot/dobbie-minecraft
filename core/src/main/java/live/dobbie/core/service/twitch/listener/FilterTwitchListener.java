package live.dobbie.core.service.twitch.listener;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.TwitchEvent;
import com.github.twitch4j.chat.events.channel.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
@ToString
public class FilterTwitchListener implements TwitchListener {
    private final @NonNull Predicate<TwitchEvent> filter;
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
    public void cleanup() {
        delegate.cleanup();
    }

    @RequiredArgsConstructor
    @ToString
    public static class ChatRoomFilter implements Predicate<TwitchEvent> {
        private final @NonNull String channelName;

        @Override
        public boolean test(TwitchEvent twitchEvent) {
            if (twitchEvent instanceof IRCMessageEvent) {
                Optional<String> eventChannelName = ((IRCMessageEvent) twitchEvent).getChannelName();
                return eventChannelName.isPresent() && channelName.equals(eventChannelName.get());
            }
            return twitchEvent instanceof AbstractChannelEvent &&
                    ((AbstractChannelEvent) twitchEvent).getChannel().getName().equals(channelName);
        }
    }
}
