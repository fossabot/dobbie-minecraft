package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.misc.TextLocation;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPatternCmdParser implements CmdParser {
    private static final String DEFAULT_SHEBANG = "#!";
    private static final Pattern DEFAULT_SHEBANG_PATTERN = Pattern.compile("#!([^\\s]+)(?s:[\\n ](.+))?");

    private final String shebang;
    private final Pattern pattern;

    public AbstractPatternCmdParser(@NonNull String shebang, @NonNull Pattern pattern) {
        this.shebang = shebang;
        this.pattern = pattern;
    }

    public AbstractPatternCmdParser() {
        this(DEFAULT_SHEBANG, DEFAULT_SHEBANG_PATTERN);
    }

    @Override

    public final Cmd parse(@NonNull Text text) throws ParserException {
        String str = text.getString();
        if (!str.startsWith(shebang)) {
            return null;
        }
        Matcher m = pattern.matcher(str);
        if (!m.matches()) {
            throw new ParserException("invalid shebang command");
        }
        String name = m.group(1);
        String args = m.groupCount() == 2 && StringUtils.isNotBlank(m.group(2)) ? m.group(2) : null;
        return parse(name, args, text.getLocation());
    }

    protected abstract Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException;

    public static abstract class NameAware extends AbstractPatternCmdParser {
        private final @NonNull List<String> acceptableNameList;

        public NameAware(@NonNull String shebang, @NonNull Pattern pattern, @NonNull List<String> acceptableNameList) {
            super(shebang, pattern);
            this.acceptableNameList = acceptableNameList;
        }

        public NameAware(@NonNull List<String> acceptableNameList) {
            super();
            this.acceptableNameList = acceptableNameList;
        }

        @Override

        protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
            if (!acceptableNameList.contains(name)) {
                return null;
            }
            try {
                return createCmd(args, location);
            } catch (ParserException e) {
                throw new ParserException("could not parse shebang command at " + location, e);
            }
        }


        protected abstract Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException;

        @NonNull
        protected static String notNull(String args) throws ParserException {
            if (args == null) {
                throw new ParserException(new NullPointerException());
            }
            return args;
        }
    }
}
