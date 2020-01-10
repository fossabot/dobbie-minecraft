package live.dobbie.core.service.twitch.data;

import live.dobbie.core.context.primitive.StringPrimitive;
import live.dobbie.core.context.primitive.converter.PrimitiveConverter;
import live.dobbie.core.trigger.authored.Author;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TwitchUser implements Author {
    @NonNull String id;
    @NonNull String name;
    @NonNull String displayName;

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
