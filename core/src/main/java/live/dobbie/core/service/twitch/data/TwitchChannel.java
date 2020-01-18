package live.dobbie.core.service.twitch.data;

import live.dobbie.core.context.primitive.converter.ConvertableToString;
import live.dobbie.core.service.twitch.NameCache;
import lombok.NonNull;

@ConvertableToString(usingMethod = "getName")
public class TwitchChannel extends TwitchUser {
    public TwitchChannel(@NonNull String id, @NonNull String name, @NonNull String displayName) {
        super(id, name, displayName);
    }

    public static TwitchChannel fromTwitch4J(@NonNull com.github.twitch4j.common.events.domain.EventChannel channel,
                                             @NonNull NameCache nameCache) {
        String login = channel.getName() == null ? nameCache.requireLogin(channel.getId()) : channel.getName();
        return new TwitchChannel(
                channel.getId(),
                login,
                nameCache.getDisplayNameOrLogin(channel.getId(), login)
        );
    }
}
