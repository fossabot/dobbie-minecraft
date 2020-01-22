package live.dobbie.core.service.streamelements;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.misc.currency.ICUFormatCurrencyFormatter;
import live.dobbie.core.service.streamelements.data.StreamElementsUser;
import live.dobbie.core.service.streamelements.events.StreamElementsEvent;
import live.dobbie.core.service.streamelements.events.StreamElementsLoyaltyStoreRedemptionEvent;
import live.dobbie.core.service.streamelements.events.StreamElementsTipEvent;
import live.dobbie.core.service.streamelements.socket.StreamElementsSocket;
import live.dobbie.core.service.streamelements.trigger.StreamElementsLoyaltyStoreRedemption;
import live.dobbie.core.service.streamelements.trigger.StreamElementsTip;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.PlainMessage;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;

import java.time.Instant;
import java.util.Objects;

public class StreamElementsSource extends Source.UsingQueue {
    private static final ILogger LOGGER = Logging.getLogger(StreamElementsSource.class);

    public static final Currency LOYALTY_POINTS_CURRENCY = Currency.register("stream_elements_loyalty_points",
            new ICUFormatCurrencyFormatter("{amount} {amount, plural, =1 {loyalty point} other {loyalty points}}", "stream_elements_loyalty_points")
    );

    private final @NonNull SettingsSubscription<StreamElementsSettings> subscription;
    private final @NonNull Loc loc;
    private final @NonNull CancellationHandler cancellationHandler;

    private StreamElementsSocket socket;

    public StreamElementsSource(@NonNull User user,
                                @NonNull ISettings settings,
                                @NonNull Loc loc,
                                @NonNull CancellationHandler cancellationHandler) {
        super(user);
        this.loc = loc;
        this.cancellationHandler = cancellationHandler;
        this.subscription = settings.registerListener(StreamElementsSettings.class, this::settingsUpdated);
    }

    void settingsUpdated(StreamElementsSettings settings) {
        LOGGER.debug("Settings updated: " + settings);
        cleanupSocket();
        if (settings == null) {
            return;
        }
        if (!settings.isEnabled()) {
            LOGGER.debug("Not enabled in the config");
            return;
        }
        socket = new StreamElementsSocket(user.getName(), this::onSocketEvent);
        socket.setToken(settings.getJwtToken());
    }

    void onSocketEvent(StreamElementsEvent event) {
        LOGGER.debug("StreamElements event received: " + event);
        try {
            if (event instanceof StreamElementsTipEvent) {
                onTipEvent((StreamElementsTipEvent) event);
            } else if (event instanceof StreamElementsLoyaltyStoreRedemptionEvent) {
                onLoyaltyStoreRedemptionEvent((StreamElementsLoyaltyStoreRedemptionEvent) event);
            } else {
                throw new RuntimeException("Unknown event: " + event);
            }
        } catch (RuntimeException rE) {
            LOGGER.warning("Could not process event: " + event, rE);
            user.sendErrorLocMessage(loc.withKey("Sorry, but Dobbie failed to process a StreamElements event." +
                    " You should check the list of the recent events."));
        }
    }

    void onTipEvent(StreamElementsTipEvent tipEvent) {
        if (!subscription.getValue().getEvents().getTip().isEnabled()) {
            LOGGER.debug("tip events are not enabled in config");
            return;
        }
        StreamElementsTipEvent.EventData data = Objects.requireNonNull(tipEvent.getData(), "tip data");
        push(new StreamElementsTip(
                user,
                Instant.now(),
                new StreamElementsUser(data.getUsername()),
                PlainMessage.of(data.getMessage()),
                Price.of(data.getAmount(), data.getCurrency()),
                subscription.getValue().getEvents().getTip().getDestination(),
                cancellationHandler
        ));
    }

    void onLoyaltyStoreRedemptionEvent(StreamElementsLoyaltyStoreRedemptionEvent redemptionEvent) {
        if (!subscription.getValue().getEvents().getLoyaltyStoreRedemption().isEnabled()) {
            LOGGER.debug("loyalty store redemption events are not enabled in config");
            return;
        }
        StreamElementsLoyaltyStoreRedemptionEvent.EventData data = Objects.requireNonNull(redemptionEvent.getData(), "redemption data");
        push(new StreamElementsLoyaltyStoreRedemption(
                user,
                Instant.now(),
                new StreamElementsUser(data.getUsername()),
                data.getRedemption(),
                new Price(data.getAmount(), LOYALTY_POINTS_CURRENCY),
                PlainMessage.of(data.getMessage()),
                subscription.getValue().getEvents().getLoyaltyStoreRedemption().getDestination(),
                cancellationHandler
        ));
    }

    private void cleanupSocket() {
        if (socket != null) {
            socket.cleanup();
            socket = null;
        }
    }

    @Override
    public void cleanup() {
        subscription.cancelSubscription();
        cleanupSocket();
    }
}
