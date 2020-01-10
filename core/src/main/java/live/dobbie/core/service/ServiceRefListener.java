package live.dobbie.core.service;


public interface ServiceRefListener<S extends Service> {
    default void onReferenceUpdated(S service, Exception error) {
        onReferenceUpdated(service);
    }

    void onReferenceUpdated(S service);
}
