package live.dobbie.core.util.logging;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@RequiredArgsConstructor
public class PrefixLogger implements ILogger {
    private final @NonNull ILogger delegate;
    private @Setter
    String prefix;

    public PrefixLogger(@NonNull ILogger delegate, String prefix) {
        this.delegate = delegate;
        setPrefix(prefix);
    }

    private String addPrefix(String message) {
        return (prefix == null ? "" : prefix) + message;
    }

    @Override
    public void tracing(String message, Throwable t) {
        delegate.tracing(addPrefix(message), t);
    }

    @Override
    public void debug(String message, Throwable t) {
        delegate.debug(addPrefix(message), t);
    }

    @Override
    public void info(String message, Throwable t) {
        delegate.info(addPrefix(message), t);
    }

    @Override
    public void warning(String message, Throwable t) {
        delegate.warning(addPrefix(message), t);
    }

    @Override
    public void error(String message, Throwable t) {
        delegate.error(addPrefix(message), t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        delegate.fatal(addPrefix(message), t);
    }

    @Override
    public void tracing(String message) {
        delegate.tracing(addPrefix(message));
    }

    @Override
    public void debug(String message) {
        delegate.debug(addPrefix(message));
    }

    @Override
    public void info(String message) {
        delegate.info(addPrefix(message));
    }

    @Override
    public void warning(String message) {
        delegate.warning(addPrefix(message));
    }

    @Override
    public void error(String message) {
        delegate.error(addPrefix(message));
    }

    @Override
    public void fatal(String message) {
        delegate.fatal(addPrefix(message));
    }
}
