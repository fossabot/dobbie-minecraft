package live.dobbie.core.substitutor.environment;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.substitutor.VarProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContextEnv implements Env {
    private final @NonNull ObjectContext context;
    private VarProvider.OfPrimitiveStorage primitiveVarProxy;

    @Override
    public <T> T get(@NonNull Class<? extends T> clazz) {
        if (clazz == VarProvider.class) {
            if (primitiveVarProxy == null) {
                primitiveVarProxy = new VarProvider.OfPrimitiveStorage(context);
            }
            return (T) primitiveVarProxy;
        }
        return null;
    }
}
