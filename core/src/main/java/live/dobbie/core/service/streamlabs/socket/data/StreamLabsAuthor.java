package live.dobbie.core.service.streamlabs.socket.data;

import live.dobbie.core.context.primitive.converter.ConvertableToString;
import live.dobbie.core.loc.Gender;
import live.dobbie.core.trigger.authored.Author;
import lombok.NonNull;
import lombok.Value;

@Value
@ConvertableToString(usingMethod = "getTarget")
public class StreamLabsAuthor implements Author {
    @NonNull String name;

    @Override
    public @NonNull Gender getGender() {
        return Gender.UNKNOWN;
    }

    public static StreamLabsAuthor of(@NonNull String name) {
        return new StreamLabsAuthor(name);
    }
}
