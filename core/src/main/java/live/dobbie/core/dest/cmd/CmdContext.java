package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;


@Value
public class CmdContext {
    User user;
    Trigger trigger;
    @NonNull ObjectContext objectContext;
    @NonNull PlainCmd.Executor executor;
    @NonNull Env environment;

    public CmdContext(User user,
                      Trigger trigger,
                      @NonNull ObjectContext objectContext,
                      @NonNull PlainCmd.Executor executor,
                      @NonNull Env environment) {
        this.user = user;
        this.trigger = trigger;
        this.objectContext = objectContext;
        this.executor = executor;
        this.environment = environment;
    }

    public CmdContext(@NonNull ObjectContext objectContext, @NonNull PlainCmd.Executor executor, @NonNull Env environment) {
        this(null, null, objectContext, executor, environment);
    }
}
