package live.dobbie.core.dictionary.sql.pool;

import lombok.NonNull;

import java.sql.SQLException;

public interface SQLConnectionPoolFactory {
    SQLConnectionPool createPool(@NonNull SQLConnectionPoolParameters params) throws SQLException;
}
