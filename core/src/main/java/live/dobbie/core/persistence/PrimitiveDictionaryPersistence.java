package live.dobbie.core.persistence;

import live.dobbie.core.dictionary.PrimitiveDictionary;
import live.dobbie.core.dictionary.PrimitiveDictionaryFactory;
import live.dobbie.core.exception.ParserRuntimeException;
import live.dobbie.core.exception.StorageException;
import live.dobbie.core.misc.primitive.NullPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.user.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface PrimitiveDictionaryPersistence extends Persistence {
    @RequiredArgsConstructor
    class Delegated implements PrimitiveDictionaryPersistence {
        private final @NonNull PrimitiveDictionary delegate;
        private final @NonNull
        @Getter
        String name;

        @Override
        public void cleanup() {
            delegate.cleanup();
        }

        @Override
        public Object get(@NonNull String key) throws StorageException {
            return delegate.get(key).getValue();
        }

        @Override
        public void set(@NonNull String key, Object object) throws StorageException {
            Primitive primitive;
            try {
                primitive = Primitive.of(object);
            } catch (ParserRuntimeException e) {
                throw new StorageException("permitted objects are the ones that can be converted to " + Primitive.class, e);
            }
            delegate.set(key, primitive);
        }

        @Override
        public void remove(@NonNull String key) throws StorageException {
            delegate.set(key, NullPrimitive.INSTANCE);
        }

        @Override
        public boolean contains(@NonNull String key) throws StorageException {
            return delegate.exists(key);
        }
    }

    @RequiredArgsConstructor
    class FactoryDelegated implements Persistence.Factory {
        private final @NonNull PrimitiveDictionaryFactory delegate;
        private final @NonNull String name;

        @NonNull
        @Override
        public Persistence create(@NonNull User user) {
            PrimitiveDictionary storage;
            try {
                storage = delegate.create(user);
            } catch (StorageException e) {
                throw new RuntimeException("could not create dictionary storage for " + user, e);
            }
            return new Delegated(storage, name);
        }
    }
}
