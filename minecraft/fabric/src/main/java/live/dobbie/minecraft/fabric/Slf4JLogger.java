package live.dobbie.minecraft.fabric;

import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.ILoggerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class Slf4JLogger implements ILogger {
    private final @NonNull Logger logger;

    @Override
    public void tracing(String message, Throwable t) {
        logger.trace(message, t);
        //info(message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        //logger.debug(message, t);
        info(message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        error(message, t);
    }

    public static class Factory implements ILoggerFactory<Slf4JLogger> {
        @Override
        public Slf4JLogger getLogger(String name) {
            return new Slf4JLogger(LoggerFactory.getLogger(name));
        }

        @Override
        public Slf4JLogger getLogger(Class clazz) {
            return new Slf4JLogger(LoggerFactory.getLogger(clazz));
        }
    }
}
