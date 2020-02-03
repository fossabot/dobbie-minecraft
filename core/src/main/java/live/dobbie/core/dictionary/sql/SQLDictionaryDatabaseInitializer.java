package live.dobbie.core.dictionary.sql;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDictionaryDatabaseInitializer {
    void initialize(@NonNull Connection connection) throws SQLException;
}
