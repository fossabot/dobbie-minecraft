package live.dobbie.core.dest.cmd;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;

public class WaitCmd extends AbstractSubstitutorCmd {
    private static final ILogger LOGGER = Logging.getLogger(WaitCmd.class);

    private final @NonNull WaitStrategy waitStrategy;

    public WaitCmd(@NonNull Substitutable substitutable, @NonNull WaitStrategy waitStrategy) {
        super(substitutable);
        this.waitStrategy = waitStrategy;
    }

    @Override
    protected CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException {
        long millis = parseMillisValue(command);
        try {
            waitStrategy.sleep(millis);
        } catch (InterruptedException e) {
            throw new CmdExecutionException("wait strategy was interrupted", e);
        }
        return CmdResult.SHOULD_CONTINUE;
    }

    private long parseMillisValue(String input) throws CmdExecutionException {
        long value;
        try {
            value = Long.parseLong(input);
        } catch (RuntimeException rE) {
            throw new CmdExecutionException("could not parse sleep millis value", rE);
        }
        if (value < 0) {
            throw new CmdExecutionException("sleep millis value must not be negative (" + value + ")");
        }
        return value;
    }

    public interface WaitStrategy {
        WaitStrategy DEFAULT = Thread::sleep;

        void sleep(long millis) throws InterruptedException;
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        private final @NonNull WaitStrategy waitStrategy;

        public Parser(@NonNull SubstitutableParser parser, @NonNull WaitStrategy waitStrategy) {
            super(parser);
            this.waitStrategy = waitStrategy;
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new WaitCmd(substitutable, waitStrategy);
        }
    }
}
