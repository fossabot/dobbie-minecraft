package live.dobbie.core.dictionary.sql;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDictionaryDatabaseAdapter {
    @NonNull Primitive get(@NonNull Connection connection, @NonNull String key) throws SQLException;

    boolean exists(@NonNull Connection connection, @NonNull String key) throws SQLException;

    void set(@NonNull Connection connection, @NonNull String key, @NonNull Primitive value) throws SQLException;
}
