package live.dobbie.core.dictionary.sql.h2;

import live.dobbie.core.dictionary.sql.SQLDictionaryDatabaseInitializer;
import org.h2.Driver;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

import static live.dobbie.core.dictionary.sql.SQL.*;

public class H2DictionaryDatabaseInitializer implements SQLDictionaryDatabaseInitializer {
    @Override
    public void initialize(@NotNull Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" +
                " " + KEY_COLUMN + " VARCHAR_CASESENSITIVE(64) PRIMARY KEY," +
                " " + VALUE_COLUMN + " OTHER" +
                " )");
    }

    public static void registerDriver() {
        Driver.load();
    }
}
