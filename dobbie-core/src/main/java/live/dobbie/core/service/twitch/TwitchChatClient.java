package live.dobbie.core.service.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.TwitchEvent;
import live.dobbie.core.service.twitch.listener.DelegateTwitchListener;
import live.dobbie.core.service.twitch.listener.FilterTwitchListener;
import live.dobbie.core.service.twitch.listener.TwitchListener;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class TwitchChatClient implements Cleanable {
    private static final ILogger LOGGER = Logging.getLogger(TwitchChatClient.class);

    private final List<ListenerRef> listeners = new ArrayList<>();

    private final @Getter
    @NonNull TwitchInstance instance;
    private final TwitchInstanceListener instanceListener;

    private TwitchClient client;

    public TwitchChatClient(@NonNull TwitchInstance instance) {
        this.instance = instance;
        instance.registerListener(instanceListener = this::updateClient, true);
    }

    public ListenerRef registerListener(@NonNull String channel, @NonNull TwitchListener listener) {
        LOGGER.tracing("Registering listener on channel " + channel + ": " + listener);
        joinChannel(channel);
        ListenerRef listenerRef = new ListenerRef(channel, listener);
        this.listeners.add(listenerRef);
        return listenerRef;
    }

    private void unregisterListener(ListenerRef ref) {
        LOGGER.tracing("Unregistering listener: " + ref);
        leaveChannel(ref.channelName);
        listeners.remove(ref);
    }

    void updateClient(TwitchClient client) {
        this.client = client;
        if (client != null) {
            LOGGER.tracing("Subscribing to events");
            subscribeToClientEvents();
            LOGGER.tracing("Joining channels");
            for (ListenerRef listener : listeners) {
                joinChannel(listener.channelName);
            }
        }
        LOGGER.tracing("Updated according to the new settings");
    }

    void subscribeToClientEvents() {
        this.client.getChat().getEventManager().onEvent(TwitchEvent.class).subscribe(this::dispatchEvent);
    }

    private void joinChannel(@NonNull String channelName) {
        LOGGER.tracing("Joining channel " + channelName);
        if (this.client != null && !isConnectedTo(channelName)) {
            this.client.getChat().joinChannel(channelName);
            LOGGER.tracing("Joined channel " + channelName);
        }
    }

    void leaveChannel(@NonNull String channelName) {
        LOGGER.tracing("Leaving channel " + channelName);
        if (this.client != null && isConnectedTo(channelName)) {
            this.client.getChat().leaveChannel(channelName);
            LOGGER.tracing("Left channel " + channelName);
        }
    }

    private boolean isConnectedTo(String channelName) {
        return listeners.stream().anyMatch(listenerRef -> listenerRef.channelName.equals(channelName));
    }

    void dispatchEvent(TwitchEvent event) {
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
    }

    @ToString(callSuper = true)
    public class ListenerRef extends DelegateTwitchListener {
        private final @NonNull String channelName;

        private ListenerRef(@NonNull String channelName, @NonNull TwitchListener delegate) {
            super(prepareListener(delegate, channelName));
            this.channelName = channelName;
        }

        public void cleanup() {
            LOGGER.tracing("Cleanup called on " + ListenerRef.this);
            unregisterListener(ListenerRef.this);
            super.cleanup();
        }
    }

    private TwitchListener prepareListener(@NonNull TwitchListener delegate, @NonNull String channelName) {
        return new FilterTwitchListener(new FilterTwitchListener.ChatRoomFilter(channelName), delegate);
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
                        if (TwitchEvent.class.isAssignableFrom(param)) {
                            LOGGER.tracing("Added into METHOD_MAP: " + param + " -> " + method);
                            map.put(param, method);
                        } else {
                            LOGGER.warning("Argument#0 (" + param + ") of " + method + " cannot be assigned to " + TwitchEvent.class + ", ignoring");
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

    static class Creator implements Function<TwitchSettings.Global, com.github.twitch4j.TwitchClient> {
        @Override
        public com.github.twitch4j.TwitchClient apply(@NonNull TwitchSettings.Global global) {
            return TwitchClientBuilder.builder()
                    .withEnableHelix(true)
                    .withEnableChat(true)
                    .withChatAccount(new OAuth2Credential("oauth", global.getClient().getToken()))
                    .build();
        }
    }
}
