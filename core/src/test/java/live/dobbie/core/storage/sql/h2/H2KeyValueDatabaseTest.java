package live.dobbie.core.storage.sql.h2;

import live.dobbie.core.exception.StorageException;
import live.dobbie.core.storage.sql.SQLKeyValueStorage;
import live.dobbie.core.storage.sql.pool.SingleConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class H2KeyValueDatabaseTest {
    static {
        H2KeyValueDatabase.registerDriver();
    }

    private H2KeyValueDatabase db;

    @BeforeEach
    void setUp() throws SQLException {
        db = new H2KeyValueDatabase(SingleConnectionPool.open("jdbc:h2:mem:default"), true);
    }

    @AfterEach
    void tearDown() {
        db.cleanup();
    }

    @Test
    void putAndGetTest() throws SQLException, StorageException {
        SQLKeyValueStorage storage = db.newConnection();
        storage.set("foo", "bar");
        assertEquals("bar", storage.get("foo"));
    }

    @Test
    void existsTest() throws SQLException, StorageException {
        SQLKeyValueStorage storage = db.newConnection();
        assertFalse(storage.exists("foo"));
    }

}