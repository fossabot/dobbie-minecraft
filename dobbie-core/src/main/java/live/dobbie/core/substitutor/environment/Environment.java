package live.dobbie.core.substitutor.environment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Environment implements Env {
    private final Map<Class, Object> map;

    @Override
    public <T> T get(@NonNull Class<? extends T> clazz) {
        return (T) map.get(clazz);
    }
}
