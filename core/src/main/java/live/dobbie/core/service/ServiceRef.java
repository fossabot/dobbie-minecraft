package live.dobbie.core.service;

import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;


public interface ServiceRef<S extends Service> extends Cleanable {
    @NonNull String getName();

    @NonNull S getService() throws ServiceUnavailableException;

    void registerListener(@NonNull ServiceRefListener<S> listener, boolean callListenerAfterwards);

    default void registerListener(@NonNull ServiceRefListener<S> listener) {
        registerListener(listener, true);
    }


    default S safelyGetService() {
        try {
            return getService();
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    default boolean isAvailable() {
        return safelyGetService() != null;
    }

    interface Factory<S extends Service> {
        @NonNull ServiceRef<S> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user);

        /*@RequiredArgsConstructor
        class CachingImpl<S extends Service> implements ServiceRef.Factory<S> {
            private final Map<IUser, ServiceRef<S>> userMap = new HashMap<>();
            private final @NonNull ServiceRef.Factory<S> serviceRefFactory;

            @Override
            @NonNull
            public ServiceRef<S> createServiceRef(@NonNull IUser user) {
                ServiceRef<S> ref = findFor(user);
                if(ref == null) {
                    ref = createFor(user);
                }
                return ref;
            }

            private ServiceRef<S> findFor(IUser user) {
                return userMap.get(user);
            }

            private ServiceRef<S> createFor(IUser user) {
                ServiceRef<S> ref = serviceRefFactory.createServiceRef(user);
                userMap.put(user, ref);
                return ref;
            }
        }*/
    }
}
