package live.dobbie.core.service;

import live.dobbie.core.user.User;
import lombok.NonNull;

public interface ServiceRefProvider {
    @NonNull <S extends Service>
    ServiceRef<S> createReference(@NonNull Class<S> serviceClass, @NonNull User user);
}
