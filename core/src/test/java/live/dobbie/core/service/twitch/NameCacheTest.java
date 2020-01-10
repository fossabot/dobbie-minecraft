package live.dobbie.core.service.twitch;

import live.dobbie.core.settings.ISettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

class NameCacheTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "twitch-test", matches = "true")
    void realTest() {
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.getValue(eq(TwitchSettings.Global.class))).thenReturn(
                new TwitchSettings.Global(new TwitchSettings.Global.Client(System.getenv("twitch-test-login"), System.getenv("twitch-test-token")))
        );
        //String userId = TwitchHelixBuilder.builder().build().getUsers(settings.getValue(TwitchSettings.Global.class).getClient().getToken(), null, Collections.singletonList("streamelements")).execute().getUsers().get(0).getId();
        TwitchInstance twitchInstance = new TwitchInstance(settings);
        NameCache nameCache = new NameCache(twitchInstance);
        assertEquals("StreamElements", nameCache.getDisplayName("100135110"));
    }

}