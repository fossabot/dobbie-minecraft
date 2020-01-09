package live.dobbie.core.substitutor.plain;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.var.AnyVarElem;
import lombok.NonNull;
import lombok.Value;

@Value
public class VarSubstitutable implements Substitutable {
    @NonNull String varName;

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        AnyVarElem.Provider varProvider = env.require(AnyVarElem.Provider.class);
        String varValue = varProvider.getVar(varName);
        return varValue == null ? "" : varValue;
    }
}
