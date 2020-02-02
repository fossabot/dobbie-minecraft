package live.dobbie.core.storage.sql.pool;

import live.dobbie.core.util.Cleanable;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionPool extends Cleanable, AutoCloseable {
    Connection getConnection() throws SQLException;

    @Override
    default void cleanup() {
        try {
            close();
        } catch (Exception e) {
            throw new RuntimeException("Could not close pool " + this, e);
        }
    }
}
