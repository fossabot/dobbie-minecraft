package live.dobbie.core.storage.sql;

import live.dobbie.core.storage.sql.pool.SQLConnectionPool;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLKeyValueDatabase implements Cleanable, AutoCloseable {
    private final @NonNull SQLConnectionPool pool;
    private final boolean closePool;

    protected SQLKeyValueDatabase(@NonNull SQLConnectionPool pool, boolean closePool) throws SQLException {
        this.pool = pool;
        this.closePool = closePool;
        try (Connection connection = pool.getConnection()) {
            initialize(connection);
        }
    }

    public SQLKeyValueStorage newConnection() throws SQLException {
        return createKeyValueConnection(pool.getConnection());
    }

    protected SQLKeyValueStorage createKeyValueConnection(@NonNull Connection connection) throws SQLException {
        return new SQLKeyValueStorageImpl(connection);
    }

    protected abstract void initialize(@NonNull Connection connection) throws SQLException;

    @Override
    public void cleanup() {
        if (closePool) {
            pool.cleanup();
        }
    }

    @Override
    public void close() {
        cleanup();
    }
}
