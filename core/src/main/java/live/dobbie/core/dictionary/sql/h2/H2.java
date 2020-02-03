package live.dobbie.core.dictionary.sql.h2;

import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolParameters;
import live.dobbie.core.dictionary.sql.pool.SQLConnectionPoolParameters.SQLConnectionPoolParametersBuilder;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class H2 {

    public SQLConnectionPoolParametersBuilder inMemoryDB(String name) {
        return jdbcH2("mem:" + (name == null ? "" : name));
    }

    public SQLConnectionPoolParametersBuilder inMemoryDB() {
        return inMemoryDB(null);
    }

    public SQLConnectionPoolParametersBuilder file(File file) {
        return jdbcH2(file.toURI().toASCIIString());
    }

    private SQLConnectionPoolParametersBuilder jdbcH2(String str) {
        return SQLConnectionPoolParameters.builder().jdbcUrl("jdbc:h2:" + str);
    }
}
