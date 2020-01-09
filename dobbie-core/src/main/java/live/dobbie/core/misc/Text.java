package live.dobbie.core.misc;

import lombok.NonNull;
import lombok.Value;

@Value
public class Text {
    @NonNull String string;
    @NonNull TextLocation location;

    @NonNull
    public String getSourceName() {
        return location.getSourceName();
    }

    public int getLineNumber() {
        return location.getLineNumber();
    }

    public String getAt() {
        return location.getAt();
    }

    public static Text ofUnknown(@NonNull String str) {
        return new Text(str, TextLocation.UNKNOWN);
    }
}
