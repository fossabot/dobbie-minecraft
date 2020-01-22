package live.dobbie.core.service.streamlabs;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.misc.Price;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.streamlabs.api.StreamLabsApi;
import live.dobbie.core.service.streamlabs.api.exception.StreamlabsApiException;
import live.dobbie.core.service.streamlabs.socket.StreamLabsSocket;
import live.dobbie.core.service.streamlabs.socket.data.StreamLabsAuthor;
import live.dobbie.core.service.streamlabs.socket.event.DonationEvent;
import live.dobbie.core.service.streamlabs.socket.event.LoyaltyStoreRedemption;
import live.dobbie.core.service.streamlabs.socket.event.StreamLabsEvent;
import live.dobbie.core.service.streamlabs.socket.trigger.StreamLabsDonation;
import live.dobbie.core.service.streamlabs.socket.trigger.StreamLabsLoyaltyStoreRedemption;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.PlainMessage;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;

import java.io.IOException;
import java.time.Instant;

public class StreamLabsSource extends Source.UsingQueue {
    private static final ILogger LOGGER = Logging.getLogger(StreamLabsSource.class);

    private final @NonNull SettingsSubscription<StreamLabsSettings> subscription;
    private final @NonNull ServiceRef<StreamLabsApi> streamLabsServiceRef;
    private final @NonNull Loc loc;
    private final @NonNull CancellationHandler cancellationHandler;

    private StreamLabsSocket socket;

    public StreamLabsSource(@NonNull User user,
                            @NonNull ISettings settings,
                            @NonNull ServiceRef<StreamLabsApi> streamLabsServiceRef,
                            @NonNull Loc loc,
                            @NonNull CancellationHandler cancellationHandler) {
        super(user);
        this.streamLabsServiceRef = streamLabsServiceRef;
        this.loc = loc;
        this.cancellationHandler = cancellationHandler;
        this.subscription = settings.registerListener(StreamLabsSettings.class, this::settingsUpdated);
    }

    void settingsUpdated(StreamLabsSettings settings) {
        LOGGER.debug("Settings updated: " + settings);
        cleanupSocket();
        if (settings != null) {
            if (!settings.isEnabled()) {
                LOGGER.debug("Not enabled in the config");
                return;
            }
            String socketToken;
            try {
                socketToken = streamLabsServiceRef.getService().getSocketToken();
            } catch (IOException | StreamlabsApiException e) {
                user.sendErrorLocMessage(loc.withKey(
                        "Could not retrieve socket token from StreamLabs API, some events will not be available." +
                                " Please contact administrator."
                ));
                LOGGER.error("Could not retrieve socket token from StreamLabs API, some events" +
                        " will not be available", e);
                return;
            }
            socket = new StreamLabsSocket(user.getName(), this::onSocketEvent);
            socket.updateToken(socketToken);
        }
    }

    void onSocketEvent(StreamLabsEvent event) {
        LOGGER.debug("StreamLabs event received: " + event);
        try {
            if (event instanceof DonationEvent) {
                onDonationEvent((DonationEvent) event);
            } else if (event instanceof LoyaltyStoreRedemption) {
                onLoyaltyStoreRedemption((LoyaltyStoreRedemption) event);
            } else {
                throw new RuntimeException("Unknown event: " + event);
            }
        } catch (RuntimeException rE) {
            LOGGER.warning("Could not process event: " + event, rE);
            user.sendErrorLocMessage(loc.withKey("Sorry, but Dobbie failed to process a StreamLabs event." +
                    " You should check the list of the recent events."));
        }
    }

    void onDonationEvent(DonationEvent donationEvent) {
        if (!subscription.getValue().getEvents().getDonation().isEnabled()) {
            LOGGER.debug("donation events are not enabled in config");
            return;
        }
        for (DonationEvent.Donation donationEventMessage : donationEvent.getMessages()) {
            LOGGER.debug("processing message: " + donationEventMessage);
            push(new StreamLabsDonation(
                    user,
                    Instant.now(),
                    new StreamLabsAuthor(donationEventMessage.getFrom()),
                    PlainMessage.of(donationEventMessage.getMessage()),
                    Price.of(donationEventMessage.getAmount(), donationEventMessage.getCurrency()),
                    subscription.getValue().getEvents().getDonation().getDestination(),
                    cancellationHandler
            ));
        }
    }

    void onLoyaltyStoreRedemption(LoyaltyStoreRedemption redemptionEvent) {
        if (!subscription.getValue().getEvents().getLoyaltyStoreRedemption().isEnabled()) {
            LOGGER.debug("loyalty store redemption events are not enabled in config");
            return;
        }

        for (LoyaltyStoreRedemption.Product product : redemptionEvent.getProducts()) {
            LOGGER.debug("processing product: " + product);
            push(new StreamLabsLoyaltyStoreRedemption(
                    user,
                    Instant.now(),
                    new StreamLabsAuthor(product.getFrom()),
                    product.getProduct(),
                    subscription.getValue().getEvents().getLoyaltyStoreRedemption().getDestination(),
                    cancellationHandler
            ));
        }
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
        // streamLabsServiceRef.cleanup();
    }
}
