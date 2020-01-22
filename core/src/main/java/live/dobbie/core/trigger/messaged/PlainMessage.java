package live.dobbie.core.trigger.messaged;

import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang.StringUtils;

@Value
public class PlainMessage implements Message {
    @NonNull String plainString;

    @Override
    public @NonNull String toPlainString() {
        return plainString;
    }

    public static PlainMessage of(String message) {
        return StringUtils.isEmpty(message) ? null : new PlainMessage(message);
    }
}
