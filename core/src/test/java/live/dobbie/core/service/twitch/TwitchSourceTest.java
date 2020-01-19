package live.dobbie.core.service.twitch;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.core.config.LoggingConfig;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.service.twitch.data.trigger.TwitchMessage;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import live.dobbie.icu.util.ULocale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TwitchSourceTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "twitch-test", matches = "true")
    void realTest() throws InterruptedException {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(TwitchSettings.Global.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        TwitchInstance instance = new TwitchInstance(settings);
        TwitchClient client = new TwitchClient(instance, new NameCache(instance));
        instance.onSettingsUpdated(new TwitchSettings.Global(new TwitchSettings.Global.Client(System.getenv("twitch-test-login"), System.getenv("twitch-test-token"))));
        TwitchSource source = new TwitchSource(client, Mockito.mock(CancellationHandler.class), Mockito.mock(User.class), settings, new NameCache(instance));
        source.updateSettings(new TwitchSettings.Player(
                true, System.getenv("twitch-test-channel"), System.getenv("twitch-test-token"),
                new LoggingConfig(new LoggingConfig.User(false), new LoggingConfig.Console(false)),
                new TwitchSettings.Events(
                        new TwitchSettings.Events.SubscriptionEventConfig(
                                null, true, new TwitchSettings.Events.SubscriptionEventConfig.Tiers(
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true)
                        )
                        ),
                        new TwitchSettings.Events.SubscriptionEventConfig(
                                null, true, new TwitchSettings.Events.SubscriptionEventConfig.Tiers(
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true),
                                new TwitchSettings.Events.SubscriptionEventConfig.TierConfig(null, true)
                        )
                        ),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true)
                        /*new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.EventConfig(null, false),
                        new TwitchSettings.Events.EventConfig(null, true),
                        new TwitchSettings.Events.CommandEventConfig(Collections.emptyList())*/
                        )
                )
        );
        Loc loc = new Loc();
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                List<Trigger> triggers = source.triggerList();
                if (triggers.isEmpty()) {
                    continue;
                }
                triggers.forEach(trigger -> {
                    if (trigger instanceof TwitchMessage) {
                        return;
                    }
                    System.out.println(trigger.toLocString(loc));
                });
            }
        });
        t.start();
        Thread.sleep(Long.parseLong(System.getenv("twitch-test-duration")));
        t.interrupt();
    }

    @Test
    void bitsCurrencyFormatUSTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ULocale.US);
        Currency currency = TwitchSource.BITS_CURRENCY;
        assertEquals("1 bit", currency.format(1, locale));
        assertEquals("2 bits", currency.format(2, locale));
    }

    @Test
    void bitsCurrencyFormatRUTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ULocale.forLanguageTag("ru-RU"));
        Currency currency = TwitchSource.BITS_CURRENCY;
        assertEquals("1 bit", currency.format(1, locale));
        assertEquals("2 bits", currency.format(2, locale));
    }

    @Test
    void bitsCurrencyFormatUnknownTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ULocale.forLanguageTag(""));
        Currency currency = TwitchSource.BITS_CURRENCY;
        assertEquals("1 bit", currency.format(1, locale));
        assertEquals("5 bits", currency.format(5, locale));
    }

}