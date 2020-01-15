package live.dobbie.core.dest.cmd;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.core.util.logging.LoggingLevel;
import lombok.NonNull;

public class SendCmd extends AbstractSubstitutorCmd {
    private static final ILogger LOGGER = Logging.getLogger(SendCmd.class);

    protected final boolean isError;

    public SendCmd(@NonNull Substitutable substitutable, boolean isError) {
        super(substitutable);
        this.isError = isError;
    }

    @Override
    protected CmdResult execute(@NonNull CmdContext context, @NonNull String message) throws CmdExecutionException {
        User user = context.getUser();
        if (user == null) {
            LoggingLevel level = isError ? LoggingLevel.ERROR : LoggingLevel.INFO;
            LOGGER.log(level, "Command context does not contain the user reference");
            LOGGER.log(level, "Message: \"" + message + "\"");
        } else {
            sendMessageToUser(context, user, message);
        }
        return isError ? CmdResult.SHOULD_STOP : CmdResult.SHOULD_CONTINUE;
    }

    protected void sendMessageToUser(@NonNull CmdContext context, @NonNull User user, @NonNull String message) {
        if (isError) {
            user.sendErrorMessage(message);
        } else {
            user.sendMessage(message);
        }
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        private final boolean isError;

        public Parser(@NonNull SubstitutableParser parser, boolean isError) {
            super(parser);
            this.isError = isError;
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new SendCmd(substitutable, isError);
        }
    }
}
