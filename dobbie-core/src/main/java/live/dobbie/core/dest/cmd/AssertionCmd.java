package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.primitive.BoolPrimitive;
import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.TextLocation;
import live.dobbie.core.path.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AssertionCmd implements Cmd {
    private final @NonNull Path varName;
    private final boolean expected;

    @Override
    @NonNull
    public CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        Primitive var;
        try {
            var = context.getObjectContext().requireVariable(varName);
        } catch (IllegalArgumentException notFound) {
            throw new CmdExecutionException("could not find requested variable for assertion", notFound);
        }
        if (var instanceof BoolPrimitive) {
            BoolPrimitive boolVar = (BoolPrimitive) var;
            if (boolVar.getBooleanValue() == expected) {
                return CmdResult.SHOULD_CONTINUE;
            } else {
                throw new AssertionCmdFailedException(Path.toString(varName) + " != " + expected);
            }
        }
        throw new CmdExecutionException("expected variable " + Path.toString(varName) + " to be boolean primitive; got " + var);
    }

    public static class Parser extends AbstractPatternCmdParser.NameAware {
        private static final String DEFAULT_PATH_SEPARATOR = Path.SEPARATOR;

        private final @NonNull String pathSeparator;

        public Parser(@NonNull String shebang, @NonNull Pattern pattern, @NonNull List<String> acceptableNameList, @NonNull String pathSeparator) {
            super(shebang, pattern, acceptableNameList);
            this.pathSeparator = pathSeparator;
        }

        public Parser(@NonNull List<String> acceptableNameList, @NonNull String pathSeparator) {
            super(acceptableNameList);
            this.pathSeparator = pathSeparator;
        }

        public Parser(@NonNull String shebang, @NonNull Pattern pattern, @NonNull List<String> acceptableNameList) {
            this(shebang, pattern, acceptableNameList, DEFAULT_PATH_SEPARATOR);
        }

        public Parser(@NonNull List<String> acceptableNameList) {
            this(acceptableNameList, DEFAULT_PATH_SEPARATOR);
        }

        @Override
        protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
            if (args == null || args.equals("!")) {
                throw new ParserException("assertion variable is not specified");
            }
            boolean expected = true;
            if (args.startsWith("!")) {
                args = args.substring(1);
                expected = false;
            }
            return new AssertionCmd(Path.parse(args, pathSeparator), expected);
        }
    }
}
