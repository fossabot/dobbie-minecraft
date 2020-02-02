package live.dobbie.core.storage.sql;

import live.dobbie.core.exception.StorageException;

public class SQLStorageException extends StorageException {
    public SQLStorageException(String message) {
        super(message);
    }

    public SQLStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLStorageException(Throwable cause) {
        super(cause);
    }
}
