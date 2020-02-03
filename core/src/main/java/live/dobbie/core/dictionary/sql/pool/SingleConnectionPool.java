package live.dobbie.core.dictionary.sql.pool;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RequiredArgsConstructor
public class SingleConnectionPool implements SQLConnectionPool {
    private final @Getter
    @NonNull Connection connection;

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    public static SingleConnectionPool open(@NonNull String jdbcUrl, String username, String password) throws SQLException {
        return new SingleConnectionPool(DriverManager.getConnection(jdbcUrl, username, password));
    }

    public static SingleConnectionPool open(@NonNull String jdbcUrl) throws SQLException {
        return open(jdbcUrl, null, null);
    }
}
