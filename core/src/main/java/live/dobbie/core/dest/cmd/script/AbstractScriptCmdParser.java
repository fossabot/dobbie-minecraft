package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.AbstractPatternCmdParser;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.script.*;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractScriptCmdParser<S extends Script<C>, C extends ScriptContext> extends AbstractPatternCmdParser.NameAware {
    protected final @NonNull ScriptExecutor<S, C> executor;
    protected final @NonNull ScriptContext.Factory<C> contextFactory;
    private final @NonNull ScriptCompiler<S> compiler;

    public AbstractScriptCmdParser(@NonNull String shebang, @NonNull Pattern pattern,
                                   @NonNull List<String> acceptableNameList,
                                   @NonNull ScriptExecutor<S, C> executor,
                                   @NonNull ScriptContext.Factory<C> contextFactory,
                                   @NonNull ScriptCompiler<S> compiler) {
        super(shebang, pattern, acceptableNameList);
        this.executor = executor;
        this.contextFactory = contextFactory;
        this.compiler = compiler;
    }

    public AbstractScriptCmdParser(@NonNull List<String> acceptableNameList,
                                   @NonNull ScriptExecutor<S, C> executor,
                                   @NonNull ScriptContext.Factory<C> contextFactory,
                                   @NonNull ScriptCompiler<S> compiler) {
        super(acceptableNameList);
        this.executor = executor;
        this.contextFactory = contextFactory;
        this.compiler = compiler;
    }

    final S compileScript(Text text) throws ParserException {
        try {
            return compiler.compile(ScriptSource.fromText(text));
        } catch (ScriptCompilationException | IOException e) {
            throw new ParserException(e);
        }
    }

}
