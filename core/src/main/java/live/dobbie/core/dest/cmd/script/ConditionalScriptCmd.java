package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.script.Script;
import live.dobbie.core.script.ScriptContext;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptExecutor;
import lombok.NonNull;

public abstract class ConditionalScriptCmd<S extends Script<C>, C extends ScriptContext> extends ScriptCmd<S, C> {
    public ConditionalScriptCmd(@NonNull ScriptExecutor<S, C> executor,
                                ScriptContext.@NonNull Factory<C> contextFactory,
                                @NonNull S script) {
        super(executor, contextFactory, script);
    }

    @Override
    protected void executeScript(@NonNull CmdContext cmdContext, @NonNull C context) throws ScriptExecutionException, CmdExecutionException {
        boolean result = executor.executeBoolean(script, context);
        processScriptResult(cmdContext, result);
    }

    protected abstract void processScriptResult(@NonNull CmdContext cmdContext, boolean result) throws CmdExecutionException;
}
