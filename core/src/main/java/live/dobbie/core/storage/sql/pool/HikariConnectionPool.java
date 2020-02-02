package live.dobbie.core.storage.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionPool implements SQLConnectionPool {
    private final @NonNull HikariDataSource source;

    public HikariConnectionPool(@NonNull String jdbcUrl,
                                String username,
                                String password) {
        this(toHikariConfig(jdbcUrl, username, password));
    }

    public HikariConnectionPool(@NonNull String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    HikariConnectionPool(@NonNull HikariConfig config) {
        this.source = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    private static HikariConfig toHikariConfig(@NonNull String jdbcUrl,
                                               String username,
                                               String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        if (username != null) {
            config.setUsername(username);
        }
        if (password != null) {
            config.setPassword(password);
        }
        return config;
    }

    @Override
    public void close() {
        source.close();
    }
}
