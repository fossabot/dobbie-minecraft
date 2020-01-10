package live.dobbie.core.util.logging;

import lombok.experimental.Delegate;

public enum LoggingLevel implements LogCaller {
    TRACING(ILogger::tracing),
    DEBUG(ILogger::debug),
    INFO(ILogger::info),
    WARNING(ILogger::warning),
    ERROR(ILogger::error),
    FATAL(ILogger::fatal);

    private final @Delegate
    LogCaller logCaller;

    LoggingLevel(LogCaller logCaller) {
        this.logCaller = logCaller;
    }
}
