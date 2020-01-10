package live.dobbie.core.substitutor.environment;

import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

public interface EnvFactory {
    @NonNull Env generateEnv(@NonNull Trigger trigger);
}
