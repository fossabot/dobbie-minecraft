package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.substitutor.environment.ContextEnv;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CmdContextFactory {
    private final @NonNull ObjectContextFactory objectContextFactory;
    // private final @NonNull EnvFactory envFactory;
    private final @NonNull PlainCmd.Executor executor;

    public CmdContext generateContext(@NonNull Trigger trigger) {
        ObjectContext objectContext = objectContextFactory.generateContextBuilder(trigger).build();
        return new CmdContext(
                UserRelatedTrigger.getUser(trigger),
                trigger,
                objectContext,
                executor,
                new ContextEnv(objectContext) // TODO envFactory.generateEnv(trigger)
        );
    }
}
