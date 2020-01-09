package live.dobbie.core.service.twitch.listener;

import com.github.twitch4j.chat.events.channel.*;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface TwitchListener extends Cleanable {
    void onIRCMessage(@NonNull IRCMessageEvent event);

    void onMessage(@NonNull ChannelMessageEvent event);

    void onFollow(@NonNull FollowEvent event);

    void onRaid(@NonNull RaidEvent event);

    void onCheer(@NonNull CheerEvent event);

    void onGiftSubscription(@NonNull GiftSubscriptionsEvent event);

    void onSubscription(@NonNull SubscriptionEvent event);
}
