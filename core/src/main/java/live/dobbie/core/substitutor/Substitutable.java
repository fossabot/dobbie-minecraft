package live.dobbie.core.substitutor;

import live.dobbie.core.substitutor.environment.Env;
import lombok.NonNull;

public interface Substitutable {
    @NonNull String substitute(@NonNull Env env);
}
