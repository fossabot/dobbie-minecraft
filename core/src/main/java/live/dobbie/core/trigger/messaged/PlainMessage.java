package live.dobbie.core.trigger.messaged;

import lombok.NonNull;
import lombok.Value;

@Value
public class PlainMessage implements Message {
    @NonNull String plainString;

    @Override
    public @NonNull String toPlainString() {
        return plainString;
    }

    public static PlainMessage of(String message) {
        return message == null ? null : new PlainMessage(message);
    }
}
