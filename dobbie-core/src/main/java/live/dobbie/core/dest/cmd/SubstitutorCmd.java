package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@Value
public class SubstitutorCmd implements Cmd {
    @NonNull Substitutable substitutable;

    @Override
    @NonNull
    public CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        return context.getExecutor().execute(context, substitutable.substitute(context.getEnvironment()));
    }

    @RequiredArgsConstructor
    public static class Parser implements CmdParser {
        private final @NonNull SubstitutableParser parser;

        @Override
        public SubstitutorCmd parse(@NonNull Text text) throws ParserException {
            return new SubstitutorCmd(parser.parse(text.getString()));
        }
    }
}
