package live.dobbie.core.util.logging;

import lombok.NonNull;

public class Logging {
    private static ILoggerFactory loggerFactory = new PlainLogger.Factory();
    static LoggingLevel LEVEL = LoggingLevel.INFO;

    public static ILogger getLogger(String name) {
        return loggerFactory.getLogger(name);
    }

    public static ILogger getLogger(Class clazz) {
        return loggerFactory.getLogger(clazz);
    }

    public static void setLoggerFactory(@NonNull ILoggerFactory factory) {
        loggerFactory = factory;
    }

    private Logging() {
    }
}
