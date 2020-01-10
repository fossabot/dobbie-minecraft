package live.dobbie.core.dest.cmd;

import live.dobbie.core.dest.DestSection;
import live.dobbie.core.dest.DestSectionLocator;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.TextLocation;
import live.dobbie.core.path.Path;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.regex.Pattern;

@Value
@EqualsAndHashCode(of = "destSectionPath")
public class GotoDestSectionCmd implements Cmd {
    @NonNull Path destSectionPath;
    @NonNull DestSectionLocator.Factory destSectionLocatorFactory;
    @NonNull DestSectionLocator fallbackSectionLocator;
    CmdResult forcedResult;

    @Override
    public @NonNull CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        DestSection destSection;
        if (context.getUser() == null) {
            destSection = fallbackSectionLocator.requireSection(destSectionPath);
        } else {
            destSection = destSectionLocatorFactory.create(context.getUser()).requireSection(destSectionPath);
        }
        CmdResult actualResult = Cmd.executeFrom(destSection.getCommands(), context);
        return forcedResult == null ? actualResult : forcedResult;
    }

    public static class Parser extends AbstractPatternCmdParser.NameAware {
        private static final String DEFAULT_PATH_SEPARATOR = Path.SEPARATOR;

        private final @NonNull DestSectionLocator.Factory destSectionLocatorFactory;
        private final @NonNull DestSectionLocator fallbackSectionLocator;
        private final CmdResult forcedResult;
        private final @NonNull String pathSeparator;

        public Parser(@NonNull String shebang, @NonNull Pattern pattern,
                      @NonNull List<String> acceptableNameList,
                      @NonNull DestSectionLocator.Factory destSectionLocatorFactory,
                      @NonNull DestSectionLocator fallbackSectionLocator,
                      CmdResult forcedResult,
                      @NonNull String pathSeparator) {
            super(shebang, pattern, acceptableNameList);
            this.destSectionLocatorFactory = destSectionLocatorFactory;
            this.fallbackSectionLocator = fallbackSectionLocator;
            this.forcedResult = forcedResult;
            this.pathSeparator = pathSeparator;
        }

        public Parser(@NonNull List<String> acceptableNameList,
                      @NonNull DestSectionLocator.Factory destSectionLocatorFactory,
                      @NonNull DestSectionLocator fallbackSectionLocator,
                      CmdResult forcedResult,
                      @NonNull String pathSeparator) {
            super(acceptableNameList);
            this.destSectionLocatorFactory = destSectionLocatorFactory;
            this.fallbackSectionLocator = fallbackSectionLocator;
            this.forcedResult = forcedResult;
            this.pathSeparator = pathSeparator;
        }

        public Parser(@NonNull String shebang, @NonNull Pattern pattern,
                      @NonNull List<String> acceptableNameList,
                      @NonNull DestSectionLocator.Factory destSectionLocatorFactory,
                      @NonNull DestSectionLocator fallbackSectionLocator) {
            this(shebang, pattern, acceptableNameList, destSectionLocatorFactory, fallbackSectionLocator, null, DEFAULT_PATH_SEPARATOR);
        }

        public Parser(@NonNull List<String> acceptableNameList,
                      @NonNull DestSectionLocator.Factory destSectionLocatorFactory,
                      @NonNull DestSectionLocator fallbackSectionLocator) {
            this(acceptableNameList, destSectionLocatorFactory, fallbackSectionLocator, null, DEFAULT_PATH_SEPARATOR);
        }

        @Override
        protected GotoDestSectionCmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
            if (args == null) {
                throw new ParserException("goto command requires an argument");
            }
            return new GotoDestSectionCmd(
                    Path.parse(args, pathSeparator),
                    destSectionLocatorFactory,
                    fallbackSectionLocator,
                    forcedResult
            );
        }
    }
}
