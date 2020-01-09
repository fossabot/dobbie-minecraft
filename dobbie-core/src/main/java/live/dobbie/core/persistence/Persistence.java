package live.dobbie.core.persistence;

import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

// TODO persistent primitive storage
public interface Persistence extends Cleanable {
    @NonNull String getName();

    interface Factory<P extends Persistence> {
        @NonNull P create(@NonNull User user);

        interface Provider {
            @NonNull <P extends Persistence> Factory<P> getFactory(Class<P> key);
        }
    }
}
