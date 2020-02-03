package live.dobbie.core.dictionary.sql;

import live.dobbie.core.dictionary.PrimitiveDictionary;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPool;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class SQLDictionaryDatabase implements Cleanable, PrimitiveDictionary {
    private final @NonNull SQLConnectionPool pool;
    private final @NonNull SQLDictionaryDatabaseAdapter storage;

    @NotNull
    @Override
    public Primitive get(@NonNull String key) throws SQLStorageException {
        try (Connection connection = newConnection()) {
            return storage.get(connection, key);
        } catch (SQLException e) {
            throw new SQLStorageException("could not get key: " + key, e);
        }
    }

    @Override
    public boolean exists(@NonNull String key) throws SQLStorageException {
        try (Connection connection = newConnection()) {
            return storage.exists(connection, key);
        } catch (SQLException e) {
            throw new SQLStorageException("could not check if key exist: " + key, e);
        }
    }

    @Override
    public void set(@NonNull String key, @NonNull Primitive value) throws SQLStorageException {
        try (Connection connection = newConnection()) {
            storage.set(connection, key, value);
        } catch (SQLException e) {
            throw new SQLStorageException("could not set key: " + key, e);
        }
    }

    private Connection newConnection() throws SQLException {
        return pool.getConnection();
    }

    @Override
    public void cleanup() {
        pool.cleanup();
    }
}
