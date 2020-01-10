package live.dobbie.core.substitutor.environment;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.substitutor.old.var.AnyVarElem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ContextEnv implements Env {
    private final @NonNull ObjectContext context;
    private VarProxy varProxy;

    @Override
    public <T> T get(@NonNull Class<? extends T> clazz) {
        if (clazz == AnyVarElem.Provider.class) {
            if (varProxy == null) {
                varProxy = initVarProxy();
            }
            return (T) varProxy;
        }
        return null;
    }

    private VarProxy initVarProxy() {
        return new VarProxy(context.getVariables());
    }

    @RequiredArgsConstructor
    private static class VarProxy implements AnyVarElem.Provider {
        private final @NonNull Map<Path, Primitive> vars;

        @Override

        public String getVar(@NonNull String name) {
            Path path = Path.parse(name, ".");
            Primitive value = vars.get(path);
            return Primitive.toString(value);
        }
    }
}
