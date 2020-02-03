package live.dobbie.core.dictionary.sql.pool;

import live.dobbie.core.user.User;
import lombok.NonNull;

public interface UserSQLConnectionPoolParametersProvider {
    @NonNull SQLConnectionPoolParameters getParametersOf(@NonNull User user);
}
