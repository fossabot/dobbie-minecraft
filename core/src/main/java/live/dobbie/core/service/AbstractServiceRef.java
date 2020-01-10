package live.dobbie.core.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AbstractServiceRef<S extends Service> implements ServiceRef<S> {
    private final List<ServiceRefListener<S>> listeners = new ArrayList<>();

    private final @NonNull
    @Getter
    String name;
    private final @NonNull
    @Getter
    ServiceRefProvider provider;

    private S service;
    private ServiceUnavailableException unavailabilityReason;
    private boolean cleanedUp;

    @NonNull
    @Override
    public S getService() throws ServiceUnavailableException {
        checkCleanedUp();
        if (unavailabilityReason != null) {
            throw unavailabilityReason;
        }
        if (service == null) {
            throw new ServiceUnavailableException("no service initialized");
        }
        return service;
    }

    @Override
    public boolean isAvailable() {
        return !cleanedUp && service != null;
    }

    @Override
    public void registerListener(@NonNull ServiceRefListener<S> listener, boolean callListenerAfterwards) {
        checkCleanedUp();
        listeners.add(listener);
        if (callListenerAfterwards) {
            listener.onReferenceUpdated(service, unavailabilityReason);
        }
    }

    @Override
    public void cleanup() {
        if (cleanedUp) {
            return;
        }
        cleanedUp = true;
        listeners.clear();
        cleanupService();
    }

    protected void checkCleanedUp() {
        if (cleanedUp) {
            throw new ServiceUnavailableException("reference cleaned up");
        }
    }

    protected void fireUpdate(S service, ServiceUnavailableException exception) {
        this.service = service;
        this.unavailabilityReason = exception;
        listeners.forEach(listener -> listener.onReferenceUpdated(service, exception));
    }

    protected void cleanupService() {
        if (service != null) {
            service.cleanup();
        }
    }
}
