package live.dobbie.core.util.logging;

import lombok.NonNull;

public interface ILoggerFactory<L extends ILogger> {
    L getLogger(String name);

    default L getLogger(@NonNull Class clazz) {
        return getLogger(clazz.getName());
    }
}
