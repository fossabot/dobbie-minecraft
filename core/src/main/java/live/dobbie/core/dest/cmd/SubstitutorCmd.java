package live.dobbie.core.dest.cmd;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import lombok.NonNull;


public class SubstitutorCmd extends AbstractSubstitutorCmd {

    public SubstitutorCmd(@NonNull Substitutable substitutable) {
        super(substitutable);
    }

    @Override
    protected CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException {
        return context.getExecutor().execute(context, command);
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        public Parser(@NonNull SubstitutableParser parser) {
            super(parser);
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new SubstitutorCmd(substitutable);
        }
    }
}
