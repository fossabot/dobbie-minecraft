package live.dobbie.core.dictionary.sql.pool;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SQLConnectionPoolParameters {
    @NonNull String jdbcUrl;
    String username, password;
}
