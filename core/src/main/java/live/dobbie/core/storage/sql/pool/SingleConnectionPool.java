package live.dobbie.core.storage.sql.pool;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnectionPool implements SQLConnectionPool {
    private final @NonNull
    @Getter
    DelegateConnection connection;

    public SingleConnectionPool(@NonNull Connection connection) {
        this(new DelegateConnection(connection));
    }

    SingleConnectionPool(@NonNull DelegateConnection connection) {
        this.connection = connection;
    }

    @Override
    public void close() throws SQLException {
        connection.getDelegate().close();
    }

    public static SingleConnectionPool open(@NonNull String jdbcUrl, String username, String password) throws SQLException {
        return new SingleConnectionPool(new DelegateConnection(DriverManager.getConnection(jdbcUrl, username, password)));
    }

    public static SingleConnectionPool open(@NonNull String jdbcUrl) throws SQLException {
        return open(jdbcUrl, null, null);
    }

    @Value
    public static class DelegateConnection implements Connection {
        private final @NonNull
        @Getter
        @Delegate(excludes = Closeable.class)
        Connection delegate;

        @Override
        public void close() {
        }
    }
}
