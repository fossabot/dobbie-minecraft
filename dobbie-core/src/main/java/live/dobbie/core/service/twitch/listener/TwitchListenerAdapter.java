package live.dobbie.core.service.twitch.listener;

import com.github.twitch4j.chat.events.channel.*;
import lombok.NonNull;

public class TwitchListenerAdapter implements TwitchListener {
    @Override
    public void onIRCMessage(@NonNull IRCMessageEvent event) {
    }

    @Override
    public void onMessage(@NonNull ChannelMessageEvent event) {
    }

    @Override
    public void onFollow(@NonNull FollowEvent event) {
    }

    @Override
    public void onRaid(@NonNull RaidEvent event) {
    }

    @Override
    public void onCheer(@NonNull CheerEvent event) {
    }

    @Override
    public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
    }

    @Override
    public void onSubscription(@NonNull SubscriptionEvent event) {
    }

    @Override
    public void cleanup() {
    }
}
