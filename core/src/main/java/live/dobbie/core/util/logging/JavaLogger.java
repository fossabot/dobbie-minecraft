package live.dobbie.core.util.logging;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class JavaLogger implements ILogger {
    private final @NonNull
    @Getter
    Logger logger;

    private void log(Level level, String message, Throwable t) {
        logger.log(level, message, t);
    }

    @Override
    public void tracing(String message, Throwable t) {
        log(Level.FINER, message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        log(Level.FINE, message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        log(Level.INFO, message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        log(Level.SEVERE, message, t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        log(Level.SEVERE, message, t);
    }

    public static class Factory implements ILoggerFactory<JavaLogger> {
        @Override
        public JavaLogger getLogger(String name) {
            return new JavaLogger(Logger.getLogger(name));
        }
    }
}
