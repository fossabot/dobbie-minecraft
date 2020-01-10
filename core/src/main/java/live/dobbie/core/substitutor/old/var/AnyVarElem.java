package live.dobbie.core.substitutor.old.var;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.factory.VarAbstractElemFactory;
import lombok.NonNull;
import lombok.Value;


@Value
public class AnyVarElem implements Substitutable {
    @NonNull String name;

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        Provider varProvider = env.require(Provider.class);
        String var = varProvider.getVar(name);
        return var == null ? "" : var;
    }

    public interface Provider {
        String getVar(@NonNull String name);

        @NonNull
        default String requireVar(@NonNull String name) {
            String var = getVar(name);
            if (var == null) {
                throw new IllegalArgumentException("variable not found: \"" + name + "\"");
            }
            return var;
        }
    }

    public static class Factory extends VarAbstractElemFactory<AnyVarElem> {
        public Factory() {
            super(null);
        }

        @Override
        protected AnyVarElem createElem(String markerName) {
            return new AnyVarElem(markerName);
        }
    }
}
