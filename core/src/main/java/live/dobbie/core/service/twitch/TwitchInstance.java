package live.dobbie.core.service.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TwitchInstance implements Cleanable {
    private static final ILogger LOGGER = Logging.getLogger(TwitchInstance.class);

    private final ArrayList<TwitchInstanceListener> listeners = new ArrayList<>();

    private final @NonNull TwitchClientFactory factory;
    private final @NonNull
    @Getter(AccessLevel.PACKAGE)
    SettingsSubscription<TwitchSettings.Global> subscription;
    private @Getter(AccessLevel.PACKAGE)
    TwitchClient client;

    public TwitchInstance(@NonNull ISettings settings, @NonNull TwitchClientFactory factory) {
        this.factory = factory;
        this.subscription = settings.registerListener(TwitchSettings.Global.class, this::onSettingsUpdated);
    }

    public TwitchInstance(@NonNull ISettings settings) {
        this(settings, TwitchClientFactory.DEFAULT);
    }

    public void sendMessage(@NonNull TwitchChannel channel, @NonNull String message) {
        LOGGER.debug("Sending message into channel " + channel + ": " + message);
        client.getChat().sendMessage(channel.getName(), message);
    }

    void registerListener(@NonNull TwitchInstanceListener listener, boolean fireImmediately) {
        this.listeners.add(listener);
        if (fireImmediately) {
            listener.onClientUpdated(client);
        }
    }

    void unregisterListener(@NonNull TwitchInstanceListener listener) {
        this.listeners.remove(listener);
    }

    void onSettingsUpdated(TwitchSettings.Global global) {
        cleanupClient();
        TwitchClient newClient;
        if (global == null) {
            newClient = null;
        } else {
            newClient = factory.create(global);
            setupClient(newClient, global);
        }
        fireNewClient(newClient);
    }

    private void setupClient(@NonNull TwitchClient client, @NonNull TwitchSettings.Global global) {
        client.getChat().leaveChannel(global.getClient().getLogin());
    }

    private void cleanupClient() {
        if (client != null) {
            client.getChat().disconnect();
        }
    }

    private void fireNewClient(TwitchClient newClient) {
        this.client = newClient;
        listeners.forEach(listener -> listener.onClientUpdated(newClient));
    }

    @Override
    public void cleanup() {
        subscription.cancelSubscription();
        cleanupClient();
    }

    public interface TwitchClientFactory {
        TwitchClientFactory DEFAULT = global ->
                TwitchClientBuilder.builder()
                        .withEnableHelix(true)
                        .withEnableChat(true)
                        .withEnablePubSub(true)
                        .withEventManager(DobbieTwitch4jEventManager.create())
                        .withChatAccount(new OAuth2Credential("oauth", global.getClient().getToken()))
                        .build();

        @NonNull TwitchClient create(@NonNull TwitchSettings.Global global);
    }
}
