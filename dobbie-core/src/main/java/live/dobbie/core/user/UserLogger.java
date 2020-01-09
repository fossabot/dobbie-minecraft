package live.dobbie.core.user;

import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.PrefixLogger;
import lombok.NonNull;

public class UserLogger extends PrefixLogger {
    public UserLogger(@NonNull ILogger delegate, @NonNull User user) {
        super(delegate, "[user:" + user.getName() + "] ");
    }
}
