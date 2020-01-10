package live.dobbie.core.script;

import live.dobbie.core.context.ObjectContext;
import lombok.NonNull;

public interface ScriptContext {
    interface Factory<C extends ScriptContext> {
        @NonNull C create(@NonNull ObjectContext context);
    }
}
