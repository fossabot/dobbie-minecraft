package live.dobbie.core.dictionary.sql;

import live.dobbie.core.dictionary.PrimitiveDictionaryFactory;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPool;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolFactory;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolParameters;
import live.dobbie.core.dictionary.sql.pool.UserSQLConnectionPoolParametersProvider;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class SQLDictionaryDatabaseFactory implements PrimitiveDictionaryFactory {
    private final @NonNull UserSQLConnectionPoolParametersProvider poolParametersProvider;
    private final @NonNull SQLDictionaryDatabaseInitializer initializer;
    private final @NonNull SQLDictionaryDatabaseAdapter adapter;
    private final @NonNull SQLConnectionPoolFactory poolFactory;

    @Override
    public SQLDictionaryDatabase create(@NonNull User user) throws SQLStorageException {
        return create(poolParametersProvider.getParametersOf(user));
    }

    private SQLDictionaryDatabase create(@NonNull SQLConnectionPoolParameters parameters) throws SQLStorageException {
        SQLConnectionPool pool = createPool(parameters);
        setupDatabase(pool);
        return new SQLDictionaryDatabase(pool, adapter);
    }

    private SQLConnectionPool createPool(SQLConnectionPoolParameters parameters) throws SQLStorageException {
        try {
            return poolFactory.createPool(parameters);
        } catch (SQLException sql) {
            throw new SQLStorageException("could not create pool using params: " + parameters, sql);
        }
    }

    private void setupDatabase(SQLConnectionPool pool) throws SQLStorageException {
        try (Connection connection = pool.getConnection()) {
            initializer.initialize(connection);
        } catch (SQLException e) {
            throw new SQLStorageException("could not get a connection from the pool", e);
        }
    }
}
