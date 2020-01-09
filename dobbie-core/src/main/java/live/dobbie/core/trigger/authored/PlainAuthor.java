package live.dobbie.core.trigger.authored;

import lombok.NonNull;
import lombok.Value;

@Value
public class PlainAuthor implements Author {
    @NonNull String name;
}
