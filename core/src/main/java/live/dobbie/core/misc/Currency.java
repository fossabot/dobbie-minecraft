package live.dobbie.core.misc;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;
import lombok.Value;

@Value
public class Currency {
    @NonNull String value;

    @JsonCreator
    public Currency(@NonNull String value) {
        this.value = value.toUpperCase();
    }
}
