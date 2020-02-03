package live.dobbie.core.dictionary.sql;

import live.dobbie.core.dictionary.sql.h2.H2;
import live.dobbie.core.dictionary.sql.h2.H2DictionaryDatabaseInitializer;
import live.dobbie.core.misc.primitive.NullPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class H2PlainSQLDictionaryDatabaseAdapterTest {

    private PlainSQLDictionaryDatabaseAdapter adapter = new PlainSQLDictionaryDatabaseAdapter();
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(H2.inMemoryDB().build().getJdbcUrl());
        new H2DictionaryDatabaseInitializer().initialize(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void emptyTest() throws SQLException {
        assertFalse(adapter.exists(connection, "foo"));
        assertEquals(NullPrimitive.INSTANCE, adapter.get(connection, "foo"));
    }

    @Test
    void putAndGetTest() throws SQLException {
        adapter.set(connection, "foo", Primitive.of("bar"));
        assertEquals(Primitive.of("bar"), adapter.get(connection, "foo"));
        assertTrue(adapter.exists(connection, "foo"));
    }

    @Test
    void putAndRemoveTest() throws SQLException {
        adapter.set(connection, "foo", Primitive.of("bar"));
        adapter.set(connection, "foo", NullPrimitive.INSTANCE);
        assertFalse(adapter.exists(connection, "foo"));
    }
}