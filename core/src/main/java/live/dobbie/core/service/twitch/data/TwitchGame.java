package live.dobbie.core.service.twitch.data;

import com.github.twitch4j.helix.domain.Game;
import lombok.NonNull;
import lombok.Value;

@Value
public class TwitchGame {
    @NonNull String id, name;
    String imageTemplate;

    public static TwitchGame fromTwitch4J(@NonNull Game game) {
        return new TwitchGame(game.getId(), game.getName(), game.getBoxArtUrl());
    }
}
