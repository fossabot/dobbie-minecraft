package live.dobbie.core.service.twitch;

import com.github.twitch4j.TwitchClient;


public interface TwitchInstanceListener {
    void onClientUpdated(TwitchClient client);
}
