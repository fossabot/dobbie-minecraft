package live.dobbie.core.dictionary.sql;

import live.dobbie.core.misc.primitive.NullPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static live.dobbie.core.dictionary.sql.SQL.*;

public class PlainSQLDictionaryDatabaseAdapter implements SQLDictionaryDatabaseAdapter {

    @NotNull
    @Override
    public Primitive get(@NonNull Connection connection, @NonNull String key) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT)) {
            stmt.setString(1, key);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Object object = resultSet.getObject(1);
                if (object instanceof Primitive) {
                    return (Primitive) object;
                }
                if (object == null) {
                    return NullPrimitive.INSTANCE;
                }
                throw new SQLException("unknown object returned on key " + key);
            }
            return NullPrimitive.INSTANCE;
        }
    }

    @Override
    public boolean exists(@NonNull Connection connection, @NonNull String key) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS)) {
            stmt.setString(1, key);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        }
    }

    @Override
    public void set(@NonNull Connection connection, @NonNull String key, @NonNull Primitive value) throws SQLException {
        Primitive existingValue = get(connection, key);
        if (Objects.equals(existingValue, value)) {
            return;
        }
        if (existingValue instanceof NullPrimitive) {
            insert(connection, key, value);
        } else {
            if (value instanceof NullPrimitive) {
                delete(connection, key);
            } else {
                update(connection, key, value);
            }
        }
    }

    private void update(Connection connection, String key, Primitive value) throws SQLException {
        insertOrUpdate(connection, key, value, 2, 1, UPDATE);
    }

    private void insert(Connection connection, String key, Primitive value) throws SQLException {
        insertOrUpdate(connection, key, value, 1, 2, INSERT);
    }

    private void delete(Connection connection, String key) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
            stmt.setString(1, key);
            executeUpdate(stmt);
        }
    }

    private void insertOrUpdate(Connection connection, String key, Primitive value, int keyIndex, int valueIndex, String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(keyIndex, key);
            stmt.setObject(valueIndex, value);
            executeUpdate(stmt);
        }
    }

    private void executeUpdate(PreparedStatement stmt) throws SQLException {
        int executeUpdate = stmt.executeUpdate();
        if (executeUpdate != 1) {
            throw new SQLException("executeUpdate returned " + executeUpdate);
        }
    }
}
