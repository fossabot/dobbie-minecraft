package live.dobbie.core.misc;

import com.fasterxml.jackson.core.JsonLocation;
import lombok.NonNull;
import lombok.Value;

@Value
public class TextLocation {
    public static final TextLocation UNKNOWN = new TextLocation("<unknown>", -1);

    @NonNull String sourceName;
    int lineNumber;
    @NonNull String at;

    public TextLocation(@NonNull String sourceName, int lineNumber) {
        this.sourceName = sourceName;
        this.lineNumber = lineNumber;
        this.at = computeAt();
    }

    private String computeAt() {
        return "(source: " + sourceName + "; line " + lineNumber + ")";
    }

    public static TextLocation of(@NonNull JsonLocation jsonLocation) {
        return new TextLocation(jsonLocation.sourceDescription(), jsonLocation.getLineNr());
    }
}
