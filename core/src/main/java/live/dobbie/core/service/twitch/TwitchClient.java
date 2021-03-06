package live.dobbie.core.service.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.EventManager;
import com.github.philippheuer.events4j.domain.Event;
import com.github.twitch4j.pubsub.PubSubSubscription;
import live.dobbie.core.service.twitch.listener.DelegateTwitchListener;
import live.dobbie.core.service.twitch.listener.FilterTwitchListener;
import live.dobbie.core.service.twitch.listener.TwitchListener;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TwitchClient implements Cleanable {
    private static final ILogger LOGGER = Logging.getLogger(TwitchClient.class);

    private final List<ListenerRef> listeners = new CopyOnWriteArrayList<>();

    private final @Getter
    @NonNull TwitchInstance instance;
    private final @NonNull NameCache nameCache;
    private final @NonNull ChannelOnlineObserver onlineObserver;
    private final TwitchInstanceListener instanceListener;

    private com.github.twitch4j.TwitchClient client;

    public TwitchClient(@NonNull TwitchInstance instance, @NonNull NameCache nameCache, @NonNull ChannelOnlineObserver onlineObserver) {
        this.instance = instance;
        this.nameCache = nameCache;
        this.onlineObserver = onlineObserver;
        instance.registerListener(instanceListener = this::updateClient, true);
    }

    public ListenerRef registerListener(@NonNull String channel, @NonNull String accessToken, @NonNull TwitchListener listener) {
        LOGGER.tracing("Registering listener on channel " + channel + ": " + listener);
        PubSubSubscription subscription = joinChannel(channel, accessToken);
        ListenerRef listenerRef = new ListenerRef(channel, accessToken, subscription, listener);
        this.listeners.add(listenerRef);
        return listenerRef;
    }

    private void unregisterListener(ListenerRef ref) {
        LOGGER.tracing("Unregistering listener: " + ref);
        leaveChannel(ref.channelName, ref.subscription);
        listeners.remove(ref);
    }

    void updateClient(com.github.twitch4j.TwitchClient client) {
        this.client = client;
        if (client != null) {
            LOGGER.tracing("Subscribing to events");
            subscribeToClientEvents();
            LOGGER.tracing("Joining channels");
            for (ListenerRef listener : listeners) {
                joinChannel(listener.channelName, listener.accessToken);
            }
        }
        LOGGER.tracing("Updated according to the new settings");
    }

    void subscribeToClientEvents() {
        EventManager eventManager = this.client.getChat().getEventManager();
        eventManager.onEvent(com.github.twitch4j.chat.events.TwitchEvent.class).subscribe(this::dispatchEvent);
        eventManager.onEvent(com.github.twitch4j.common.events.TwitchEvent.class).subscribe(this::dispatchEvent);
    }

    private PubSubSubscription joinChannel(@NonNull String channelName, @NonNull String accessToken) {
        LOGGER.tracing("Joining channel " + channelName);
        if (this.client != null && !isConnectedTo(channelName)) {
            this.client.getChat().joinChannel(channelName);
            this.onlineObserver.startObserving(nameCache.requireId(channelName));
            LOGGER.tracing("Joined channel " + channelName);
            return subscribeToPubSub(channelName, accessToken);
        }
        return null;
    }

    private PubSubSubscription subscribeToPubSub(@NonNull String channelName, @NonNull String accessToken) {
        return this.client.getPubSub().listenForChannelPointsRedemptionEvents(
                new OAuth2Credential("oauth", accessToken),
                Long.parseLong(Objects.requireNonNull(nameCache.getId(channelName), "could not get id of " + channelName))
        );
    }

    void leaveChannel(@NonNull String channelName, PubSubSubscription subscription) {
        LOGGER.tracing("Leaving channel " + channelName);
        if (this.client != null && isConnectedTo(channelName)) {
            this.client.getChat().leaveChannel(channelName);
            this.onlineObserver.stopObserving(nameCache.requireId(channelName));
            LOGGER.tracing("Left channel " + channelName);
            unsubscribeFromPubSub(subscription);
        }
    }

    private void unsubscribeFromPubSub(@Nullable PubSubSubscription subscription) {
        if (subscription != null) {
            this.client.getPubSub().unsubscribeFromTopic(subscription);
        }
    }

    private boolean isConnectedTo(String channelName) {
        return listeners.stream().anyMatch(listenerRef -> listenerRef.channelName.equals(channelName));
    }

    void dispatchEvent(Event event) {
        LOGGER.tracing("Dispatching event " + event);
        Method method = METHOD_MAP.get(event.getClass());
        if (method == null) {
            LOGGER.tracing("No method for " + event.getClass());
            return;
        }
        LOGGER.tracing("Invoking " + method);
        for (ListenerRef listener : listeners) {
            //LOGGER.tracing("Invoking on " + listener);
            try {
                method.invoke(listener, event);
            } catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Error executing " + method + " on " + listener + " using argument " + event, e);
            }
        }
    }

    @Override
    public void cleanup() {
        listeners.forEach(ListenerRef::cleanup);
        instance.unregisterListener(instanceListener);
        client = null;
    }

    @ToString(callSuper = true)
    public class ListenerRef extends DelegateTwitchListener {
        private final @NonNull String channelName, accessToken;
        private PubSubSubscription subscription;

        private ListenerRef(@NonNull String channelName,
                            @NonNull String accessToken,
                            PubSubSubscription subscription,
                            @NonNull TwitchListener delegate) {
            super(prepareListener(delegate, channelName));
            this.channelName = channelName;
            this.accessToken = accessToken;
            this.subscription = subscription;
        }

        public void cleanup() {
            LOGGER.tracing("Cleanup called on " + ListenerRef.this);
            unregisterListener(ListenerRef.this);
            super.cleanup();
        }
    }

    private TwitchListener prepareListener(@NonNull TwitchListener delegate, @NonNull String channelName) {
        return new FilterTwitchListener(new FilterTwitchListener.ChatRoomFilter(nameCache.requireId(channelName)), delegate);
    }

    private static final Map<Class, Method> METHOD_MAP;

    static {
        Map<Class, Method> map = new HashMap<>();
        Method[] declaredMethods = TwitchListener.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().startsWith("on")) {
                if (Modifier.isPublic(method.getModifiers())) {
                    if (method.getParameterCount() == 1) {
                        Class param = method.getParameterTypes()[0];
                        if (Event.class.isAssignableFrom(param)) {
                            LOGGER.tracing("Added into METHOD_MAP: " + param + " -> " + method);
                            map.put(param, method);
                        } else {
                            LOGGER.warning("Argument#0 (" + param + ") of " + method + " cannot be assigned to " + Event.class + ", ignoring");
                        }
                    } else {
                        LOGGER.warning("Method " + method + " does not have exactly 1 argument, ignoring");
                    }
                } else {
                    LOGGER.warning("Method " + method + " is not public, ignoring");
                }
            } else {
                LOGGER.warning("Method " + method + " does not start with \"on\", ignoring");
            }
        }
        LOGGER.tracing("METHOD_MAP.size(): " + map.size());
        METHOD_MAP = Collections.unmodifiableMap(map);
    }
}
