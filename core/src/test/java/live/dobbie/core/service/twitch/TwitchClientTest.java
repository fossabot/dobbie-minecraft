package live.dobbie.core.service.twitch;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.pubsub.PubSubSubscription;
import com.github.twitch4j.pubsub.TwitchPubSub;
import live.dobbie.core.service.twitch.listener.TwitchListener;
import live.dobbie.core.service.twitch.listener.TwitchListenerAdapter;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.*;

class TwitchClientTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "twitch-test", matches = "true")
    void realTest() throws InterruptedException {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(TwitchSettings.Global.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        TwitchInstance instance = new TwitchInstance(settings);
        TwitchClient client = new TwitchClient(instance, new NameCache(instance));
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client(System.getenv("twitch-test-login"), System.getenv("twitch-test-token"))));
        client.registerListener(System.getenv("twitch-test-channel"), System.getenv("twitch-test-token"), new TwitchListenerAdapter() {
            @Override
            public void onSubscription(@NonNull SubscriptionEvent event) {
                System.out.println("subscription event: " + event);
            }

            @Override
            public void onGiftSubscription(@NonNull GiftSubscriptionsEvent event) {
                System.out.println("gift subscription event: " + event);
            }

            @Override
            public void onRaid(@NonNull RaidEvent event) {
                System.out.println("raid event: " + event);
            }
        });
        Thread.sleep(Long.parseLong(System.getenv("twitch-test-duration")));
    }

    @Test
    void messageTest() throws InterruptedException {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(TwitchSettings.Global.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        TwitchInstance instance = new TwitchInstance(settings, global -> {
            TwitchChat chat = Mockito.mock(TwitchChat.class);
            com.github.twitch4j.TwitchClient client1 = Mockito.mock(com.github.twitch4j.TwitchClient.class);
            when(client1.getChat()).thenReturn(chat);
            TwitchPubSub pubSub = mock(TwitchPubSub.class);
            when(client1.getPubSub()).thenReturn(pubSub);
            return client1;
        });
        NameCache nameCache = mock(NameCache.class);
        when(nameCache.getId(eq("test"))).thenReturn("0");
        when(nameCache.requireId(eq("test"))).thenReturn("0");
        TwitchClient client = new TwitchClient(instance, nameCache) {
            @Override
            void subscribeToClientEvents() {
                // no-op
            }
        };
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client("login", "token")));
        TwitchListener listener = Mockito.mock(TwitchListener.class);
        client.registerListener("test", "token", listener);
        ChannelMessageEvent messageEvent = new ChannelMessageEvent(
                new EventChannel("0", "test"),
                new EventUser("1", "testUser"),
                "bar",
                Collections.emptySet()
        );
        client.dispatchEvent(messageEvent);
        verify(listener).onMessage(eq(messageEvent));
    }

    @Test
    void leaveUnregisteredChannelTest() throws InterruptedException {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(TwitchSettings.Global.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        TwitchInstance instance = new TwitchInstance(settings, global -> {
            TwitchChat chat = Mockito.mock(TwitchChat.class);
            com.github.twitch4j.TwitchClient client1 = Mockito.mock(com.github.twitch4j.TwitchClient.class);
            when(client1.getChat()).thenReturn(chat);
            TwitchPubSub pubSub = mock(TwitchPubSub.class);
            when(client1.getPubSub()).thenReturn(pubSub);
            when(pubSub.listenForChannelPointsRedemptionEvents(notNull(), eq(0L))).thenReturn(mock(PubSubSubscription.class));
            return client1;
        });
        NameCache nameCache = mock(NameCache.class);
        when(nameCache.getId(eq("test"))).thenReturn("0");
        when(nameCache.requireId(eq("test"))).thenReturn("0");
        TwitchClient client = Mockito.spy(new TwitchClient(instance, nameCache) {
            @Override
            void subscribeToClientEvents() {
                // no-op
            }
        });
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client("login", "token")));
        TwitchListener listener = Mockito.mock(TwitchListener.class);
        TwitchClient.ListenerRef listenerRef = client.registerListener("test", "token", listener);
        listenerRef.cleanup();
        verify(client).leaveChannel(eq("test"), any());
    }

}