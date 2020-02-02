package live.dobbie.core.storage.sql.h2;

import live.dobbie.core.storage.sql.SQLKeyValueDatabase;
import live.dobbie.core.storage.sql.pool.SQLConnectionPool;
import lombok.NonNull;
import org.h2.Driver;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

import static live.dobbie.core.storage.sql.SQLKeyValueStorage.*;

public class H2KeyValueDatabase extends SQLKeyValueDatabase {
    protected H2KeyValueDatabase(@NonNull SQLConnectionPool pool, boolean closePool) throws SQLException {
        super(pool, closePool);
    }

    @Override
    protected void initialize(@NotNull Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" +
                " " + KEY_COLUMN + " VARCHAR_CASESENSITIVE(64) PRIMARY KEY," +
                " " + VALUE_COLUMN + " VARCHAR_CASESENSITIVE(8192)" +
                " )");
    }

    public static void registerDriver() {
        Driver.load();
    }
}
