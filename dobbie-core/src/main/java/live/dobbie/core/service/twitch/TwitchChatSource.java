package live.dobbie.core.service.twitch;

import com.github.philippheuer.events4j.domain.Event;
import com.github.twitch4j.chat.enums.SubscriptionPlan;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import live.dobbie.core.misc.Currency;
import live.dobbie.core.misc.Price;
import live.dobbie.core.service.twitch.data.TwitchChannel;
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

import java.time.Instant;
import java.util.Objects;

public class TwitchChatSource extends Source.UsingQueue {
    private final ILogger LOGGER = Logging.getLogger(TwitchChatSource.class);
    private static final Currency BITS_CURRENCY = new Currency("BITS");

    private final @NonNull TwitchChatClient chatClient;
    private final @NonNull CancellationHandler<TwitchChatTrigger> cancellationHandler;
    private final NameCache nameCache;

    private final SettingsSubscription<TwitchSettings.Player> subscription;
    private final Listener listener = new Listener();
    private final UserLogger logger;

    private TwitchChatClient.ListenerRef listenerRef;
    private TwitchSettings.Player playerSettings;

    public TwitchChatSource(
            @NonNull TwitchChatClient chatClient,
            @NonNull CancellationHandler<TwitchChatTrigger> cancellationHandler,
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
                registerListener(newValue.getChannel());
            } else {
                logger.debug("Twitch is not enabled");
            }
        } else {
            logger.debug("No Twitch settings provided");
        }
        playerSettings = newValue;
    }

    private void registerListener(@NonNull String channelName) {
        listenerRef = chatClient.registerListener(channelName, listener);
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
        cancellationHandler.cleanup();
    }

    private class Listener implements TwitchListener {
        @Override
        public void onIRCMessage(@NonNull IRCMessageEvent event) {
            // logger.tracing("IRCMessage: " + event);
        }

        @Override
        public void onMessage(@NonNull ChannelMessageEvent event) {
            // logger.tracing("onMessage: " + event);
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
                // logger.tracing("onMessage [skipped]");
            }
        }

        @Override
        public void onFollow(@NonNull FollowEvent event) {
            logger.tracing("onFollow: " + event);
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
                logger.tracing("onFollow [skipped]");
            }
        }

        @Override
        public void onRaid(@NonNull RaidEvent event) {
            logger.tracing("onRaid: " + event);
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
                logger.tracing("onRaid [skipped]");
            }
        }

        @Override
        public void onCheer(@NonNull CheerEvent event) {
            logger.tracing("onCheer: " + event);
            if (playerSettings != null && playerSettings.getEvents().getCheer().isEnabled()) {
                push(new TwitchCheer(
                        user, chatClient,
                        channel(event),
                        timestamp(event),
                        user(event.getUser()),
                        new PlainMessage(event.getMessage()),
                        new Price(event.getBits(), BITS_CURRENCY),
                        prefDest(playerSettings.getEvents().getCheer()),
                        cancellationHandler
                ));
            } else {
                logger.tracing("onCheer [skipped]");
            }
        }

        @Override
        public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
            logger.tracing("onGiftSub: " + event);
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
                logger.tracing("onGiftSub [skipped]");
            }
        }

        @Override
        public void onSubscription(@NonNull SubscriptionEvent event) {
            logger.tracing("onSub: " + event);
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
                logger.tracing("onSub [skipped]");
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
        return new TwitchUser(
                eventUser.getId(),
                eventUser.getName(),
                getDisplayName(eventUser.getId(), eventUser.getName())
        );
    }

    private TwitchChannel channel(@NonNull AbstractChannelEvent event) {
        EventChannel eventChannel = Objects.requireNonNull(event.getChannel(), "event channel");
        return new TwitchChannel(
                eventChannel.getId(),
                eventChannel.getName(),
                getDisplayName(eventChannel.getId(), eventChannel.getName())
        );
    }

    @NonNull
    private String getDisplayName(String id, String fallback) {
        return nameCache == null ? fallback : nameCache.getDisplayNameOr(id, fallback);
    }
}
