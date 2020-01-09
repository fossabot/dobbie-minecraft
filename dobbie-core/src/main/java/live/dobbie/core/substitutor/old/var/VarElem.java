package live.dobbie.core.substitutor.old.var;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.factory.VarAbstractElemFactory;
import lombok.NonNull;
import lombok.Value;


@Value
public class VarElem implements Substitutable {
    @NonNull String name;
    @NonNull EnvProv provider;

    @NonNull
    @Override
    public String substitute(@NonNull Env env) {
        return provider.get(env);
    }

    public interface EnvProv {
        @NonNull String get(@NonNull Env env);
    }

    public interface Prov extends EnvProv {
        @NonNull String get();

        @Override
        @NonNull
        default String get(@NonNull Env env) {
            return get();
        }
    }

    public static class Factory extends VarAbstractElemFactory<VarElem> {
        private final @NonNull EnvProv prov;

        public Factory(@NonNull String variableName, @NonNull EnvProv prov) {
            super(variableName);
            this.prov = prov;
        }

        @Override
        protected VarElem createElem(String markerName) {
            return new VarElem(markerName, prov);
        }
    }
}
