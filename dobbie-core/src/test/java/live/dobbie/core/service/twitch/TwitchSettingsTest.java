package live.dobbie.core.service.twitch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.Settings;
import live.dobbie.core.settings.source.jackson.JacksonParser;
import live.dobbie.core.settings.source.jackson.JacksonSource;
import live.dobbie.core.util.io.StringSupplier;
import live.dobbie.core.util.io.URLSupplier;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TwitchSettingsTest {
    @Test
    void basicTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new StringSupplier("{\"services\":{\"twitch\":{\"client\":{\"login\":\"test login\",\"token\":\"test token\"}}}}"));
        source.load();
        JacksonParser.Provider provider = new JacksonParser.Provider();
        Settings settings = new Settings(source, provider);
        TwitchSettings.Global global = settings.getValue(TwitchSettings.Global.class);
        assertNotNull(global);
        assertNotNull(global.getClient(), "client");
        assertEquals("test login", global.getClient().getLogin());
        assertEquals("test token", global.getClient().getToken());
    }

    @Test
    void globalTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new ObjectMapper(new YAMLFactory()), new URLSupplier(getClass().getResource("twitch-global.yml")));
        source.load();
        JacksonParser.Provider provider = new JacksonParser.Provider();
        Settings settings = new Settings(source, provider);
        TwitchSettings.Global global = settings.getValue(TwitchSettings.Global.class);
        assertNotNull(global);
        assertNotNull(global.getClient(), "client");
        assertEquals("test login", global.getClient().getLogin());
        assertEquals("test token", global.getClient().getToken());
    }

    @Test
    void userTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new ObjectMapper(new YAMLFactory()), new URLSupplier(getClass().getResource("twitch-user.yml")));
        source.load();
        JacksonParser.Provider provider = new JacksonParser.Provider();
        Settings settings = new Settings(source, provider);
        TwitchSettings.Player player = settings.getValue(TwitchSettings.Player.class);
        assertNotNull(player);
        assertFalse(player.getLogging().getToUser().isEnabled());
        assertTrue(player.getLogging().getToConsole().isEnabled());
        assertEquals("twitch_subscription", player.getEvents().getSubscription().getDestination());
        assertTrue(player.getEvents().getSubscription().isEnabled());
        assertEquals("twitch_prime_subscription", player.getEvents().getSubscription().getTiers().getTwitchPrime().getDestination());
        assertTrue(player.getEvents().getSubscription().getTiers().getTwitchPrime().isEnabled());
        assertNull(player.getEvents().getSubscription().getTiers().getTier1().getDestination());
        assertTrue(player.getEvents().getSubscription().getTiers().getTier1().isEnabled());
        assertNull(player.getEvents().getSubscription().getTiers().getTier2().getDestination());
        assertTrue(player.getEvents().getSubscription().getTiers().getTier2().isEnabled());
        assertEquals("twitch_tier3_subscription", player.getEvents().getSubscription().getTiers().getTier3().getDestination());
        assertTrue(player.getEvents().getSubscription().getTiers().getTier3().isEnabled());
        /*assertEquals(2, player.getEvents().getCommand().getList().size());
        assertEquals("chat", player.getEvents().getCommand().getList().get(0).getName());
        assertNotNull(player.getEvents().getCommand().getList().get(0).getAliases());
        assertEquals("чат", player.getEvents().getCommand().getList().get(0).getAliases().get(0));
        assertEquals("ч", player.getEvents().getCommand().getList().get(0).getAliases().get(1));
        assertEquals("отправляет сообщение в чат", player.getEvents().getCommand().getList().get(0).getDescription());
        assertNotNull(player.getEvents().getCommand().getList().get(0).getPrice());
        assertEquals(new Currency("streamlabs_loyaty_points"), player.getEvents().getCommand().getList().get(0).getPrice().getCurrency());
        assertEquals(new Range(100., 9999.), player.getEvents().getCommand().getList().get(0).getPrice().getRanges().get(0));
        assertNotNull(player.getEvents().getCommand().getList().get(0).getArguments());
        assertEquals(TwitchSettings.Events.CommandEventConfig.CommandInfo.ArgumentConfig.PerArgumentConfig.REQUIRED, player.getEvents().getCommand().getList().get(0).getArguments().getAmount());
        assertEquals(TwitchSettings.Events.CommandEventConfig.CommandInfo.ArgumentConfig.PerArgumentConfig.REQUIRED, player.getEvents().getCommand().getList().get(0).getArguments().getMessage());
        assertNotNull(player.getEvents().getCommand().getList().get(0).getResponses());
        assertFalse(player.getEvents().getCommand().getList().get(0).getResponses().isEmpty());
        assertEquals("kick", player.getEvents().getCommand().getList().get(1).getName());
        assertNotNull(player.getEvents().getCommand().getList().get(1).getAliases());
        assertEquals("пнуть", player.getEvents().getCommand().getList().get(1).getAliases().get(0));
        assertEquals("п", player.getEvents().getCommand().getList().get(1).getAliases().get(1));
        assertEquals("пинает стримера", player.getEvents().getCommand().getList().get(1).getDescription());
        assertNotNull(player.getEvents().getCommand().getList().get(1).getPrice());
        assertEquals(new Currency("streamlabs_loyaty_points"), player.getEvents().getCommand().getList().get(1).getPrice().getCurrency());
        assertEquals(2, player.getEvents().getCommand().getList().get(1).getPrice().getRanges().size());
        assertEquals(new Range(1000.), player.getEvents().getCommand().getList().get(1).getPrice().getRanges().get(0));
        assertEquals(new Range(2000.), player.getEvents().getCommand().getList().get(1).getPrice().getRanges().get(1));
        assertNotNull(player.getEvents().getCommand().getList().get(1).getArguments());
        assertEquals(TwitchSettings.Events.CommandEventConfig.CommandInfo.ArgumentConfig.PerArgumentConfig.REQUIRED, player.getEvents().getCommand().getList().get(1).getArguments().getAmount());
        assertEquals(TwitchSettings.Events.CommandEventConfig.CommandInfo.ArgumentConfig.PerArgumentConfig.ENABLED, player.getEvents().getCommand().getList().get(1).getArguments().getMessage());
        assertNotNull(player.getEvents().getCommand().getList().get(1).getResponses());
        assertFalse(player.getEvents().getCommand().getList().get(1).getResponses().isEmpty());*/
    }
}