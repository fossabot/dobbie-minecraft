package live.dobbie.core.trigger.messaged;

import live.dobbie.core.misc.primitive.converter.ConvertableToString;
import lombok.NonNull;

@ConvertableToString(usingMethod = "toPlainString")
public interface Message {
    @NonNull String toPlainString();
}
