package live.dobbie.core.service.twitch.data;

import live.dobbie.core.misc.primitive.converter.ConvertableToString;
import live.dobbie.core.service.twitch.NameCache;
import lombok.NonNull;

@ConvertableToString(usingMethod = "getName")
public class TwitchChannel extends TwitchUser {
    public TwitchChannel(@NonNull String id, @NonNull String login, @NonNull String displayName) {
        super(id, login, displayName);
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
