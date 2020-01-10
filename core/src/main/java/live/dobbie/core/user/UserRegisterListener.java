package live.dobbie.core.user;

import live.dobbie.core.util.Cleanable;
import lombok.NonNull;

public interface UserRegisterListener extends Cleanable {
    void registerUser(@NonNull User user);

    void unregisterUser(@NonNull User user);
}
