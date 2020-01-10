package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.AssertionCmdFailedException;
import live.dobbie.core.dest.cmd.Cmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.misc.TextLocation;
import live.dobbie.core.script.Script;
import live.dobbie.core.script.ScriptCompiler;
import live.dobbie.core.script.ScriptContext;
import live.dobbie.core.script.ScriptExecutor;
import lombok.NonNull;

import java.util.List;
import java.util.regex.Pattern;

public class AssertionScriptCmdParser<S extends Script<C>, C extends ScriptContext> extends ConditionalScriptCmdParser<S, C> {
    public AssertionScriptCmdParser(@NonNull String shebang, @NonNull Pattern pattern,
                                    @NonNull List<String> acceptableNameList,
                                    @NonNull ScriptExecutor<S, C> executor,
                                    @NonNull ScriptContext.Factory<C> contextFactory,
                                    @NonNull ScriptCompiler<S> compiler) {
        super(shebang, pattern, acceptableNameList, executor, contextFactory, compiler, null);
    }

    public AssertionScriptCmdParser(@NonNull List<String> acceptableNameList,
                                    @NonNull ScriptExecutor<S, C> executor,
                                    @NonNull ScriptContext.Factory<C> contextFactory,
                                    @NonNull ScriptCompiler<S> compiler) {
        super(acceptableNameList, executor, contextFactory, compiler, null);
    }

    @Override
    protected Cmd parseEnclosingCmd(String command, @NonNull TextLocation location) throws ParserException {
        if (command != null) {
            throw new ParserException("no enclosed command allowed in assertion command");
        }
        return null;
    }

    @Override
    protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
        if (args == null) {
            throw new ParserException("condition is required in assertion command");
        }
        return new ConditionalScriptCmd<S, C>(executor, contextFactory, compileScript(new Text(args, location))) {
            @Override
            protected void processScriptResult(@NonNull CmdContext cmdContext, boolean result) throws CmdExecutionException {
                if (!result) {
                    throw new AssertionCmdFailedException("\"" + args + "\"");
                }
            }
        };
    }
}
