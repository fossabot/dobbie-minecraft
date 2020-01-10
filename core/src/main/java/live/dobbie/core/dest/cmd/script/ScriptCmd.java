package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.dest.cmd.Cmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.dest.cmd.CmdResult;
import live.dobbie.core.script.Script;
import live.dobbie.core.script.ScriptContext;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptExecutor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "script")
public class ScriptCmd<S extends Script<C>, C extends ScriptContext> implements Cmd {
    protected final @NonNull ScriptExecutor<S, C> executor;
    private final @NonNull ScriptContext.Factory<C> contextFactory;
    protected final @NonNull S script;

    @Override
    @NonNull
    public final CmdResult execute(@NonNull CmdContext cmdContext) throws CmdExecutionException {
        C context = contextFactory.create(cmdContext.getObjectContext());
        try {
            executeScript(cmdContext, context);
        } catch (ScriptExecutionException e) {
            throw new CmdExecutionException(e);
        }
        return CmdResult.SHOULD_CONTINUE;
    }

    protected void executeScript(@NonNull CmdContext cmdContext, @NonNull C context) throws ScriptExecutionException, CmdExecutionException {
        executor.execute(script, context);
    }
}
