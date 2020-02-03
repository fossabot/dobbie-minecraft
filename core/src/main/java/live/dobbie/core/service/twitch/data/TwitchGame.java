package live.dobbie.core.service.twitch.data;

import com.github.twitch4j.helix.domain.Game;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import lombok.NonNull;
import lombok.Value;

@Value
public class TwitchGame {
    @NonNull String id, name;
    String imageTemplate;

    public static TwitchGame fromTwitch4J(@NonNull Game game) {
        return new TwitchGame(game.getId(), game.getName(), game.getBoxArtUrl());
    }

    public static class Id implements PrimitiveConverter<TwitchGame, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchGame value) {
            return new StringPrimitive(value.getId());
        }
    }

    public static class Name implements PrimitiveConverter<TwitchGame, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchGame value) {
            return new StringPrimitive(value.getName());
        }
    }
}
