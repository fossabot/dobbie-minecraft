package live.dobbie.core.persistence;

import live.dobbie.core.user.User;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

public class SessionObjectStorage implements Persistence {
    private static final String NAME = "session";

    private final ConcurrentHashMap<String, Object> storage = new ConcurrentHashMap<>();


    public Object get(@NonNull String key) {
        return storage.get(key);
    }

    public void set(@NonNull String key, Object object) {
        if (object == null) {
            remove(key);
        } else {
            storage.put(key, object);
        }
    }

    public void remove(@NonNull String key) {
        storage.remove(key);
    }

    public boolean contains(@NonNull String key) {
        return storage.containsKey(key);
    }

    public boolean is(@NonNull String key, Object object) {
        if (object == null) {
            return !contains(key);
        } else {
            return object.equals(get(key));
        }
    }

    @Override
    public void cleanup() {
        storage.clear();
    }

    @Override
    public @NonNull String getName() {
        return NAME;
    }

    public static class Factory implements Persistence.Factory<SessionObjectStorage> {
        @NonNull
        @Override
        public SessionObjectStorage create(@NonNull User user) {
            return new SessionObjectStorage();
        }
    }
}
