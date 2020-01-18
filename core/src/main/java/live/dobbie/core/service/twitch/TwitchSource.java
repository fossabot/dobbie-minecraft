package live.dobbie.core.service.twitch;

import com.github.philippheuer.events4j.domain.Event;
import com.github.twitch4j.chat.enums.SubscriptionPlan;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.misc.currency.ICUFormatCurrencyFormatter;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchChannelPointsReward;
import live.dobbie.core.service.twitch.data.TwitchSubscriptionPlan;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.service.twitch.data.trigger.*;
import live.dobbie.core.service.twitch.listener.TwitchListener;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.PlainMessage;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserLogger;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class TwitchSource extends Source.UsingQueue {
    private final ILogger LOGGER = Logging.getLogger(TwitchSource.class);

    public static final Currency BITS_CURRENCY = Currency.register("bits",
            new ICUFormatCurrencyFormatter("{amount} {amount, plural, =1 {bit} other {bits}}", "bits")
    );

    public static final Currency CHANNEL_POINTS_CURRENCY = Currency.register("channel_points",
            new ICUFormatCurrencyFormatter("{amount} {amount, plural, =1 {channel point} other {channel points}}", "channel_points")
    );

    private final @NonNull TwitchClient chatClient;
    private final @NonNull CancellationHandler cancellationHandler;
    private final NameCache nameCache;

    private final SettingsSubscription<TwitchSettings.Player> subscription;
    private final Listener listener = new Listener();
    private final UserLogger logger;

    private TwitchClient.ListenerRef listenerRef;
    private TwitchSettings.Player playerSettings;

    public TwitchSource(
            @NonNull TwitchClient chatClient,
            @NonNull CancellationHandler cancellationHandler,
            @NonNull User user,
            @NonNull ISettings settings,
            NameCache nameCache) {
        super(user);
        this.chatClient = chatClient;
        this.nameCache = nameCache;
        this.cancellationHandler = cancellationHandler;
        this.logger = new UserLogger(LOGGER, user);
        this.subscription = settings.registerListener(TwitchSettings.Player.class, this::updateSettings);
    }

    void updateSettings(TwitchSettings.Player newValue) {
        logger.tracing("updateSettings");
        unregisterListener();
        if (newValue != null) {
            if (newValue.isEnabled()) {
                logger.debug("Twitch settings updated");
                registerListener(newValue.getChannel(), newValue.getAccessToken());
            } else {
                logger.debug("Twitch is not enabled");
            }
        } else {
            logger.debug("No Twitch settings provided");
        }
        playerSettings = newValue;
    }

    private void registerListener(@NonNull String channelName, @NonNull String accessToken) {
        listenerRef = chatClient.registerListener(channelName, accessToken, listener);
    }

    private void unregisterListener() {
        logger.tracing("unregisterListener");
        if (listenerRef != null) {
            listenerRef.cleanup();
        }
    }

    @Override
    public void cleanup() {
        logger.tracing("cleanup");
        unregisterListener();
        subscription.cancelSubscription();
    }

    private class Listener implements TwitchListener {
        @Override
        public void onIRCMessage(@NonNull IRCMessageEvent event) {
            // logger.tracing("IRCMessage: " + event);
        }

        @Override
        public void onMessage(@NonNull ChannelMessageEvent event) {
            logger.tracing("onMessage: " + event);
            if (playerSettings != null && playerSettings.getEvents().getChat().isEnabled()) {
                push(new TwitchMessage(
                        user, chatClient,
                        channel(event),
                        timestamp(event),
                        user(event.getUser()),
                        new PlainMessage(event.getMessage()),
                        prefDest(playerSettings.getEvents().getChat()),
                        cancellationHandler
                ));
            } else {
                logger.tracing("onMessage [skipped]");
            }
        }

        @Override
        public void onFollow(@NonNull FollowEvent event) {
            logger.debug("onFollow: " + event);
            if (playerSettings != null && playerSettings.getEvents().getFollow().isEnabled()) {
                push(new TwitchFollow(
                        user, chatClient,
                        channel(event),
                        timestamp(event),
                        user(event.getUser()),
                        prefDest(playerSettings.getEvents().getFollow()),
                        cancellationHandler
                ));
            } else {
                logger.debug("onFollow [skipped]");
            }
        }

        @Override
        public void onRaid(@NonNull RaidEvent event) {
            logger.debug("onRaid: " + event);
            if (playerSettings != null && playerSettings.getEvents().getRaid().isEnabled()) {
                push(new TwitchRaid(
                        user, chatClient,
                        channel(event),
                        timestamp(event),
                        user(event.getRaider()),
                        event.getViewers(),
                        prefDest(playerSettings.getEvents().getRaid()),
                        cancellationHandler
                ));
            } else {
                logger.debug("onRaid [skipped]");
            }
        }

        @Override
        public void onCheer(@NonNull CheerEvent event) {
            logger.debug("onCheer: " + event);
            if (playerSettings != null && playerSettings.getEvents().getCheer().isEnabled()) {
                push(new TwitchCheer(
                        user, chatClient,
                        channel(event),
                        timestamp(event),
                        user(event.getUser()),
                        new PlainMessage(event.getMessage()),
                        new Price(new BigDecimal(event.getBits()), BITS_CURRENCY),
                        prefDest(playerSettings.getEvents().getCheer()),
                        cancellationHandler
                ));
            } else {
                logger.debug("onCheer [skipped]");
            }
        }

        @Override
        public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
            logger.debug("onGiftSub: " + event);
            if (playerSettings != null && playerSettings.getEvents().getGiftSubscription().isEnabled()) {
                TwitchSubscriptionPlan twitchSubscriptionPlan = subPlan(event.getSubscriptionPlan());
                if (isSubPlanEnabled(playerSettings.getEvents().getGiftSubscription().getTiers(), twitchSubscriptionPlan.getTier())) {
                    push(new TwitchGiftSubscription(
                            user, chatClient,
                            channel(event),
                            timestamp(event),
                            user(event.getUser()),
                            event.getCount(), event.getTotalCount(), twitchSubscriptionPlan,
                            prefDest(playerSettings.getEvents().getGiftSubscription()),
                            cancellationHandler
                    ));
                }
            } else {
                logger.debug("onGiftSub [skipped]");
            }
        }

        @Override
        public void onSubscription(@NonNull SubscriptionEvent event) {
            logger.debug("onSub: " + event);
            if (playerSettings != null && playerSettings.getEvents().getSubscription().isEnabled()) {
                TwitchSubscriptionPlan twitchSubscriptionPlan = subPlan(event.getSubscriptionPlan());
                if (isSubPlanEnabled(playerSettings.getEvents().getSubscription().getTiers(), twitchSubscriptionPlan.getTier())) {
                    push(new TwitchSubscription(
                            user, chatClient,
                            channel(event),
                            timestamp(event),
                            user(event.getUser()),
                            event.getMessage().isPresent() ? new PlainMessage(event.getMessage().get()) : null,
                            new TwitchSubscriptionPlan(TwitchSubscriptionPlan.Tier.from(event.getSubscriptionPlanName())),
                            event.getMonths(), event.getSubStreak(),
                            event.getGifted(), user(event.getGiftedBy()),
                            prefDest(playerSettings.getEvents().getSubscription()),
                            cancellationHandler
                    ));
                }
            } else {
                logger.debug("onSub [skipped]");
            }
        }

        @Override
        public void onChannelPointsRedemption(@NonNull ChannelPointsRedemptionEvent event) {
            logger.debug("onChannelPoints: " + event);
            if (playerSettings != null && playerSettings.getEvents().getChannelPoints().isEnabled()) {
                push(new TwitchChannelPointsRedemption(
                        user, chatClient, channel(event), timestamp(event), user(event.getUser()),
                        TwitchChannelPointsReward.fromTwitch4j(event.getRedemption().getReward()),
                        PlainMessage.of(event.getRedemption().getUserInput()),
                        prefDest(playerSettings.getEvents().getChannelPoints()),
                        cancellationHandler
                ));
            } else {
                logger.debug("onChannelPoints [skipped]");
            }
        }

        @Override
        public void cleanup() {
        }
    }

    private static TwitchSubscriptionPlan subPlan(String subscriptionPlan) {
        return new TwitchSubscriptionPlan(TwitchSubscriptionPlan.Tier.from(SubscriptionPlan.fromString(subscriptionPlan)));
    }

    private static String prefDest(TwitchSettings.Events.EventConfig eventConfig) {
        return eventConfig.getDestination();
    }

    private static boolean isSubPlanEnabled(TwitchSettings.Events.SubscriptionEventConfig.Tiers tierConfig, TwitchSubscriptionPlan.Tier tier) {
        switch (tier) {
            case TWITCH_PRIME:
                return tierConfig.getTwitchPrime().isEnabled();
            case TIER_1:
                return tierConfig.getTier1().isEnabled();
            case TIER_2:
                return tierConfig.getTier2().isEnabled();
            case TIER_3:
                return tierConfig.getTier3().isEnabled();
        }
        return true;
    }

    private static Instant timestamp(@NonNull Event event) {
        return event.getFiredAt().toInstant();
    }

    private TwitchUser user(EventUser eventUser) {
        if (eventUser == null) {
            return null;
        }
        return TwitchUser.fromTwitch4J(eventUser, nameCache);
    }

    private TwitchChannel channel(@NonNull AbstractChannelEvent event) {
        EventChannel eventChannel = Objects.requireNonNull(event.getChannel(), "event channel");
        return TwitchChannel.fromTwitch4J(eventChannel, nameCache);
    }

    private TwitchChannel channel(@NonNull ChannelPointsRedemptionEvent event) {
        EventChannel eventChannel = Objects.requireNonNull(event.getChannel(), "event channel");
        return TwitchChannel.fromTwitch4J(eventChannel, nameCache);
    }

    @NonNull
    private String getDisplayName(String id, String fallback) {
        if (fallback == null) {
            return nameCache.getDisplayName(id);
        } else {
            return nameCache.getDisplayNameOrLogin(id, fallback);
        }
    }
}
