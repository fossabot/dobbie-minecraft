package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.Cmd;
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

public class ScriptCmdParser<S extends Script<C>, C extends ScriptContext> extends AbstractScriptCmdParser<S, C> {

    public ScriptCmdParser(@NonNull String shebang, @NonNull Pattern pattern,
                           @NonNull List<String> acceptableNameList,
                           @NonNull ScriptExecutor<S, C> executor,
                           ScriptContext.@NonNull Factory<C> contextFactory,
                           @NonNull ScriptCompiler<S> compiler) {
        super(shebang, pattern, acceptableNameList, executor, contextFactory, compiler);
    }

    public ScriptCmdParser(@NonNull List<String> acceptableNameList,
                           @NonNull ScriptExecutor<S, C> executor,
                           ScriptContext.@NonNull Factory<C> contextFactory,
                           @NonNull ScriptCompiler<S> compiler) {
        super(acceptableNameList, executor, contextFactory, compiler);
    }

    @Override
    protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
        if (args == null) {
            throw new ParserException("enclosing command is mandatory in conventional script commands");
        }
        return new ScriptCmd<>(executor, contextFactory, compileScript(new Text(args, location)));
    }
}
