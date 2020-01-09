package live.dobbie.core.substitutor.environment;

import lombok.NonNull;
import org.apache.commons.lang3.Validate;


public interface Env {
    <T> T get(@NonNull Class<? extends T> clazz);

    @NonNull
    default <T> T require(@NonNull Class<? extends T> clazz) {
        return Validate.notNull(get(clazz), "missing environment key: " + clazz);
    }
}
