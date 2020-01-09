package live.dobbie.core.util.logging;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

@RequiredArgsConstructor
public class PlainLogger implements ILogger {
    private final String prefix;

    private void log(PrintStream stream, String message, Throwable t) {
        stream.println(prefix + message);
        if (t != null) {
            t.printStackTrace(System.out);
        }
    }

    private void outLog(String message, Throwable t) {
        log(System.out, message, t);
    }

    private void errLog(String message, Throwable t) {
        log(System.err, message, t);
    }

    @Override
    public void tracing(String message, Throwable t) {
        //outLog("[TRACING] " + message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        outLog("[DEBUG] " + message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        outLog("[INFO] " + message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        errLog("[WARNING]" + message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        errLog("[ERROR] " + message, t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        errLog("[FATAL] " + message, t);
    }

    public static class Factory implements ILoggerFactory<PlainLogger> {
        @Override
        public PlainLogger getLogger(String name) {
            return new PlainLogger("[" + name + "]");
        }
    }
}
