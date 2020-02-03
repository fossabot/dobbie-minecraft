package live.dobbie.core.dictionary.sql.pool.hikari;

import live.dobbie.core.dictionary.sql.pool.SQLConnectionPool;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolFactory;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolParameters;
import lombok.NonNull;

public class HikariConnectionPoolFactory implements SQLConnectionPoolFactory {
    @Override
    public SQLConnectionPool createPool(@NonNull SQLConnectionPoolParameters params) {
        return new HikariConnectionPool(params.getJdbcUrl(), params.getUsername(), params.getPassword());
    }
}
