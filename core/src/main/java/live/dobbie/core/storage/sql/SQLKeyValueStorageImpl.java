package live.dobbie.core.storage.sql;

import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
class SQLKeyValueStorageImpl implements SQLKeyValueStorage {
    private static final ILogger LOGGER = Logging.getLogger(SQLKeyValueStorageImpl.class);

    private final @NonNull Connection connection;

    @Override
    public String get(@NonNull String key) throws SQLStorageException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT)) {
            stmt.setString(1, key);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        } catch (SQLException sql) {
            throw new SQLStorageException("error getting key " + key, sql);
        }
    }

    @Override
    public boolean exists(@NonNull String key) throws SQLStorageException {
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS)) {
            stmt.setString(1, key);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException sql) {
            throw new SQLStorageException("error checking for existence of key " + key, sql);
        }
    }

    @Override
    public void set(@NonNull String key, String value) throws SQLStorageException {
        if (exists(key)) {
            update(key, value);
        } else {
            insert(key, value);
        }
    }

    private void update(String key, String value) throws SQLStorageException {
        try {
            insertOrUpdate(key, value, 2, 1, UPDATE);
        } catch (SQLException sql) {
            throw new SQLStorageException("error updating key " + key, sql);
        }
    }

    private void insert(String key, String value) throws SQLStorageException {
        try {
            insertOrUpdate(key, value, 1, 2, INSERT);
        } catch (SQLException sql) {
            throw new SQLStorageException("error inserting key " + key, sql);
        }
    }

    private void insertOrUpdate(String key, String value, int keyIndex, int valueIndex, String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(keyIndex, key);
            stmt.setString(valueIndex, value);
            int executeUpdate = stmt.executeUpdate();
            if (executeUpdate != 1) {
                throw new SQLException("executeUpdate returned " + executeUpdate);
            }
        }
    }

    @Override
    public void cleanup() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.warning("Could not close connection: " + connection);
        }
    }
}
