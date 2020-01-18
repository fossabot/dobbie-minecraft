package live.dobbie.core.service.twitch.data;

import com.github.twitch4j.common.events.domain.EventUser;
import live.dobbie.core.context.primitive.StringPrimitive;
import live.dobbie.core.context.primitive.converter.PrimitiveConverter;
import live.dobbie.core.loc.Gender;
import live.dobbie.core.service.twitch.NameCache;
import live.dobbie.core.trigger.authored.Author;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TwitchUser implements Author {
    @NonNull String id;
    @NonNull String name;
    @NonNull String displayName;
    @NonNull Gender gender;

    public TwitchUser(@NonNull String id, @NonNull String name, @NonNull String displayName) {
        this(id, name, displayName, Gender.UNKNOWN);
    }

    public static TwitchUser fromTwitch4J(@NonNull EventUser user,
                                          @NonNull NameCache nameCache) {
        String login = user.getName() == null ? nameCache.requireLogin(user.getId()) : user.getName();
        return new TwitchUser(
                user.getId(),
                user.getName(),
                nameCache.getDisplayNameOrLogin(user.getId(), login)
        );
    }

    public static class IdConverter implements PrimitiveConverter<TwitchUser, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchUser value) {
            return new StringPrimitive(value.getId());
        }
    }

    public static class NameConverter implements PrimitiveConverter<TwitchUser, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchUser value) {
            return new StringPrimitive(value.getName());
        }
    }

    public static class DisplayNameConverter implements PrimitiveConverter<TwitchUser, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchUser value) {
            return new StringPrimitive(value.getDisplayName());
        }
    }
}
