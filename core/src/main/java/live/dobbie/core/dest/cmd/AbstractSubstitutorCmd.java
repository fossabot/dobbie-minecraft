package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractSubstitutorCmd implements Cmd {
    @NonNull Substitutable substitutable;

    @Override
    @NonNull
    public final CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        return execute(context, substitutable.substitute(context.getEnvironment()));
    }

    protected abstract CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException;

    @RequiredArgsConstructor
    public abstract static class Parser implements CmdParser {
        private final @NonNull SubstitutableParser parser;

        @Override
        public final AbstractSubstitutorCmd parse(@NonNull Text text) throws ParserException {
            Substitutable substitutable = parser.parse(text.getString());
            return create(substitutable);
        }

        protected abstract AbstractSubstitutorCmd create(@NonNull Substitutable substitutable);
    }
}
