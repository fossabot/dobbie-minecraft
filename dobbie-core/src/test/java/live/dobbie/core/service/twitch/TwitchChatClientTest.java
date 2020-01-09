package live.dobbie.core.service.twitch;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
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

class TwitchChatClientTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "twitch-test", matches = "true")
    void realTest() throws InterruptedException {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(TwitchSettings.Global.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        TwitchInstance instance = new TwitchInstance(settings);
        TwitchChatClient client = new TwitchChatClient(instance);
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client(System.getenv("twitch-test-login"), System.getenv("twitch-test-token"))));
        client.registerListener(System.getenv("twitch-test-channel"), new TwitchListenerAdapter() {
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
            return client1;
        });
        TwitchChatClient client = new TwitchChatClient(instance) {
            @Override
            void subscribeToClientEvents() {
                // no-op
            }
        };
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client("login", "token")));
        TwitchListener listener = Mockito.mock(TwitchListener.class);
        client.registerListener("test", listener);
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
            return client1;
        });
        TwitchChatClient client = Mockito.spy(new TwitchChatClient(instance) {
            @Override
            void subscribeToClientEvents() {
                // no-op
            }
        });
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client("login", "token")));
        TwitchListener listener = Mockito.mock(TwitchListener.class);
        TwitchChatClient.ListenerRef listenerRef = client.registerListener("test", listener);
        listenerRef.cleanup();
        verify(client).leaveChannel(eq("test"));
    }

}