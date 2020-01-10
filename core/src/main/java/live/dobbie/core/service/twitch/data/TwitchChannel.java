package live.dobbie.core.service.twitch.data;

import live.dobbie.core.context.primitive.converter.ConvertableToString;
import lombok.NonNull;

@ConvertableToString(usingMethod = "getName")
public class TwitchChannel extends TwitchUser {
    public TwitchChannel(@NonNull String id, @NonNull String name, @NonNull String displayName) {
        super(id, name, displayName);
    }
}
