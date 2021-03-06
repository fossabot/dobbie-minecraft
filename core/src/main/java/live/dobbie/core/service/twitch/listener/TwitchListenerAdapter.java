package live.dobbie.core.service.twitch.listener;

import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import live.dobbie.core.service.twitch.event.ChannelGoLiveEvent;
import live.dobbie.core.service.twitch.event.ChannelGoOfflineEvent;
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
    public void onChannelPointsRedemption(@NonNull ChannelPointsRedemptionEvent event) {
    }

    @Override
    public void onChannelGoLive(@NonNull ChannelGoLiveEvent event) {
    }

    @Override
    public void onChannelGoOffline(@NonNull ChannelGoOfflineEvent event) {
    }

    @Override
    public void cleanup() {
    }
}
