package live.dobbie.core.substitutor.plain;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import lombok.NonNull;
import lombok.Value;

@Value
public class StringSubstitutable implements Substitutable {
    @NonNull String value;

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        return value;
    }
}
