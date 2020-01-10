package live.dobbie.core.util.logging;

import lombok.NonNull;


interface LogCaller {
    void log(@NonNull ILogger logger, String message, Throwable t);
}
