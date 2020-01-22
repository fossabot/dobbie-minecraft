package live.dobbie.core.service.streamelements.data;

import live.dobbie.core.loc.Gender;
import live.dobbie.core.trigger.authored.Author;
import lombok.NonNull;
import lombok.Value;

@Value
public class StreamElementsUser implements Author {
    @NonNull String name;

    @Override
    public @NonNull Gender getGender() {
        return Gender.UNKNOWN;
    }
}
