package live.dobbie.core.context.storage;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class StorageAwareObjectContext implements ObjectContext, MutablePrimitiveStorage {
    private static final String STORAGE_VAR_NAME = "storage";

    private final @NonNull ObjectContext delegate;
    private final @NonNull MutablePrimitiveStorage storage;
    private final @NonNull String storageVarName;

    public StorageAwareObjectContext(@NonNull ObjectContext delegate, @NonNull MutablePrimitiveStorage storage) {
        this(delegate, storage, STORAGE_VAR_NAME);
    }

    @Override
    public @NonNull Map<String, Object> getObjects() {
        return copyAndAdd(delegate.getObjects(), storageVarName, storage);
    }

    @Override
    public <T> T getObject(@NonNull String key) {
        if (storageVarName.equals(key)) {
            return (T) storage;
        }
        return delegate.getObject(key);
    }

    @Override
    public @NonNull Map<Path, Primitive> getVariables() {
        return mergeMaps(delegate.getVariables(), storage.getVariables());
    }

    @Override
    public Primitive getVariable(@NonNull Path path) {
        Primitive value;
        value = storage.getVariable(path);
        if (value != null) {
            return value;
        }
        value = delegate.getVariable(path);
        return value;
    }

    @Override
    public void setVariable(@NonNull Path key, @NonNull Primitive value) {
        storage.setVariable(key, value);
    }

    @Override
    public void removeVariable(@NonNull Path key) {
        storage.removeVariable(key);
    }

    private static <K, V> Map<K, V> mergeMaps(Map<K, V> m0, Map<K, V> m1) {
        Map<K, V> m = new HashMap<>(m0.size() + m1.size());
        m.putAll(m0);
        m.putAll(m1);
        return m;
    }

    private static <K, V> Map<K, V> copyAndAdd(Map<K, V> m0, K key, V value) {
        Map<K, V> m = new HashMap<>(m0.size() + 1);
        m.putAll(m0);
        m.put(key, value);
        return m;
    }

    @RequiredArgsConstructor
    public static class Factory implements ObjectContextFactory {
        private final @NonNull ObjectContextFactory delegate;
        private final @NonNull MutablePrimitiveStorage.Factory storageFactory;
        private final @NonNull String storageVarName;

        public Factory(@NonNull ObjectContextFactory delegate, @NonNull MutablePrimitiveStorage.Factory storageFactory) {
            this(delegate, storageFactory, STORAGE_VAR_NAME);
        }

        @Override
        public @NonNull ObjectContextBuilder generateContextBuilder(@NonNull Trigger trigger) {
            return new Builder(delegate.generateContextBuilder(trigger), storageFactory.create(), storageVarName);
        }
    }

    @RequiredArgsConstructor
    public static class Builder implements ObjectContextBuilder {
        private final @NonNull ObjectContextBuilder delegate;
        private final @NonNull MutablePrimitiveStorage storage;
        private final @NonNull String storageVarName;

        @Override
        public ObjectContextBuilder set(@NonNull String key, @NonNull Object value) {
            delegate.set(key, value);
            return this;
        }

        @Override
        public ObjectContextBuilder set(@NonNull Path path, @NonNull Primitive primitive) {
            delegate.set(path, primitive);
            return this;
        }

        @Override
        public StorageAwareObjectContext build() {
            return new StorageAwareObjectContext(delegate.build(), storage, storageVarName);
        }
    }
}
