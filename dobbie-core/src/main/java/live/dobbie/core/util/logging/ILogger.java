package live.dobbie.core.util.logging;

import lombok.NonNull;

public interface ILogger {
    void tracing(String message, Throwable t);

    void debug(String message, Throwable t);

    void info(String message, Throwable t);

    void warning(String message, Throwable t);

    void error(String message, Throwable t);

    void fatal(String message, Throwable t);

    default void tracing(String message) {
        tracing(message, null);
    }

    default void debug(String message) {
        debug(message, null);
    }

    default void info(String message) {
        info(message, null);
    }

    default void warning(String message) {
        warning(message, null);
    }

    default void error(String message) {
        error(message, null);
    }

    default void fatal(String message) {
        fatal(message, null);
    }

    default void log(@NonNull LoggingLevel level, String message, Throwable t) {
        level.log(this, message, t);
    }

    default void log(@NonNull LoggingLevel level, String message) {
        log(level, message, null);
    }
}
