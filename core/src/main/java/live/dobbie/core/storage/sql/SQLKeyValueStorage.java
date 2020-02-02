package live.dobbie.core.storage.sql;

import live.dobbie.core.storage.KeyValueStorage;
import lombok.NonNull;

public interface SQLKeyValueStorage extends KeyValueStorage {
    String
            TABLE_NAME = "primitives",
            KEY_COLUMN = "key",
            VALUE_COLUMN = "value";

    String
            SELECT = "SELECT " + VALUE_COLUMN + " FROM " + TABLE_NAME + " WHERE " + KEY_COLUMN + " = ?;",
            EXISTS = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + KEY_COLUMN + " = ?;",
            INSERT = "INSERT INTO " + TABLE_NAME + "(" + KEY_COLUMN + ", " + VALUE_COLUMN + ") VALUES (?, ?);",
            UPDATE = "UPDATE " + TABLE_NAME + " SET " + VALUE_COLUMN + " = ? WHERE " + KEY_COLUMN + " = ?;";

    @Override
    String get(@NonNull String key) throws SQLStorageException;

    @Override
    boolean exists(@NonNull String key) throws SQLStorageException;

    @Override
    void set(@NonNull String key, String value) throws SQLStorageException;
}
