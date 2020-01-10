package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.Cmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.dest.cmd.CmdParser;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.misc.TextLocation;
import live.dobbie.core.script.Script;
import live.dobbie.core.script.ScriptCompiler;
import live.dobbie.core.script.ScriptContext;
import live.dobbie.core.script.ScriptExecutor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalScriptCmdParser<S extends Script<C>, C extends ScriptContext> extends AbstractScriptCmdParser<S, C> {
    private static final Pattern CONDITION_PATTERN = Pattern.compile("\\((.+)\\) (.+)");

    private final CmdParser enclosingParser;

    public ConditionalScriptCmdParser(@NonNull String shebang, @NonNull Pattern pattern,
                                      @NonNull List<String> acceptableNameList,
                                      @NonNull ScriptExecutor<S, C> executor,
                                      ScriptContext.@NonNull Factory<C> contextFactory,
                                      @NonNull ScriptCompiler<S> compiler,
                                      CmdParser enclosingParser) {
        super(shebang, pattern, acceptableNameList, executor, contextFactory, compiler);
        this.enclosingParser = enclosingParser;
    }

    public ConditionalScriptCmdParser(@NonNull List<String> acceptableNameList,
                                      @NonNull ScriptExecutor<S, C> executor,
                                      ScriptContext.@NonNull Factory<C> contextFactory,
                                      @NonNull ScriptCompiler<S> compiler,
                                      CmdParser enclosingParser) {
        super(acceptableNameList, executor, contextFactory, compiler);
        this.enclosingParser = enclosingParser;
    }

    @Override
    protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
        if (args == null) {
            throw new ParserException("condition is empty");
        }
        Matcher m = CONDITION_PATTERN.matcher(args);
        if (!m.matches()) {
            throw new ParserException("does not match the pattern: #!name (script condition) command");
        }
        String condition = m.group(1);
        verifyParenthesisCount(condition, location);
        String command;
        if (m.groupCount() == 2) {
            command = m.group(2);
            verifyParenthesisCount(command, location);
        } else {
            command = null;
        }
        final Cmd enclosingCommand = parseEnclosingCmd(command, location);
        return new ConditionalScriptCmd<S, C>(executor, contextFactory, compileScript(new Text(condition, location))) {
            @Override
            protected void processScriptResult(@NonNull CmdContext cmdContext, boolean result) throws CmdExecutionException {
                if (result && enclosingCommand != null) {
                    enclosingCommand.execute(cmdContext);
                }
            }
        };
    }

    protected Cmd parseEnclosingCmd(String command, @NonNull TextLocation location) throws ParserException {
        if (enclosingParser == null || command == null) {
            return null;
        }
        Cmd enclosingCmd;
        try {
            enclosingCmd = enclosingParser.parse(new Text(command, location));
            if (enclosingCmd == null) {
                throw new ParserException("enclosingParser returned null");
            }
        } catch (ParserException e) {
            throw new ParserException("could not parse enclosing command \"" + command + "\" at " + location, e);
        }
        return enclosingCmd;
    }

    private static void verifyParenthesisCount(String str, TextLocation location) throws ParserException {
        int openingCount = StringUtils.countMatches(str, '(');
        int closingCount = StringUtils.countMatches(str, ')');
        if (openingCount != closingCount) {
            throw new ParserException("parenthesis count did not match: " + openingCount + " !=" + closingCount + " in \"" + str + "\"");
        }
    }
}
