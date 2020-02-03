package live.dobbie.core.service.twitch;

import com.github.twitch4j.helix.domain.GameList;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.hystrix.HystrixCommand;
import live.dobbie.core.service.twitch.data.TwitchGame;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@RequiredArgsConstructor
public class GameCache {
    private static final Cache<String, TwitchGame> gameByIdCache = CacheBuilder.newBuilder().softValues().build();

    private final @NonNull TwitchInstance instance;

    public TwitchGame getById(@NonNull String id) throws Exception {
        TwitchGame game = gameByIdCache.getIfPresent(id);
        if (game != null) {
            return game;
        }
        TwitchGame twitchGame = requestGameInfo(instance, getToken(), id);
        gameByIdCache.put(id, twitchGame);
        return twitchGame;
    }

    private String getToken() {
        TwitchSettings.Global value = instance.getSubscription().getValue();
        return value == null ? null : value.getClient().getToken();
    }

    @NonNull
    private static TwitchGame requestGameInfo(TwitchInstance instance, String token, String id) throws Exception {
        HystrixCommand<GameList> games = instance.getClient().getHelix().getGames(token, Collections.singletonList(id), null);
        return TwitchGame.fromTwitch4J(games.execute().getGames().get(0));
    }
}
