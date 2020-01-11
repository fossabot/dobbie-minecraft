package live.dobbie.core.substitutor.plain;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.VarProvider;
import live.dobbie.core.substitutor.environment.Env;
import lombok.NonNull;
import lombok.Value;

@Value
public class VarSubstitutable implements Substitutable {
    @NonNull String varName;

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        VarProvider varProvider = env.require(VarProvider.class);
        String varValue = varProvider.getVar(varName);
        return varValue == null ? "" : varValue;
    }
}
