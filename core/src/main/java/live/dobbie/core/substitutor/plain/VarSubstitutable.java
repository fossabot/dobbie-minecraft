package live.dobbie.core.substitutor.plain;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.VarProvider;
import live.dobbie.core.substitutor.environment.Env;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor
@Value
public class VarSubstitutable implements Substitutable {
    @NonNull String varName;
    VarConverter varConverter;

    public VarSubstitutable(@NonNull String varName) {
        this(varName, null);
    }

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        VarProvider varProvider = env.require(VarProvider.class);
        String varValue = varProvider.getVar(varName);
        if (varConverter != null) {
            return varConverter.convertVarValue(varValue);
        }
        return varValue == null ? "" : varValue;
    }
}
