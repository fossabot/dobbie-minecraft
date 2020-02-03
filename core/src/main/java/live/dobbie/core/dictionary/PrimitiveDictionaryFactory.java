package live.dobbie.core.dictionary;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.user.User;
import lombok.NonNull;

public interface PrimitiveDictionaryFactory {
    PrimitiveDictionary create(@NonNull User user) throws StorageException;
}
