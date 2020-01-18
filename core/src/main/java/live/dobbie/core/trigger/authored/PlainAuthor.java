package live.dobbie.core.trigger.authored;

import live.dobbie.core.loc.Gender;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor
@Value
public class PlainAuthor implements Author {
    @NonNull String name;
    @NonNull Gender gender;

    public PlainAuthor(@NonNull String name) {
        this(name, Gender.UNKNOWN);
    }
}
