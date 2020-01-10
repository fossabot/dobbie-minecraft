package live.dobbie.core.service.chargeback;

import live.dobbie.core.persistence.StorageException;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceUnavailableException;
import live.dobbie.core.trigger.cancellable.Cancellable;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.priced.Donated;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChargebackHandler implements CancellationHandler {
    private static final ILogger LOGGER = Logging.getLogger(ChargebackHandler.class);

    private final @NonNull ServiceRef<ChargebackService> serviceRef;

    @Override
    public void cancel(@NonNull Cancellable cancellable, @NonNull Cancellation cancellation) {
        Donated donated = checkIfDonated(cancellable);
        if (donated == null) {
            LOGGER.tracing("Not Donated: " + cancellable);
            return;
        }
        LOGGER.tracing("Donated and Cancellable: " + donated);
        try {
            serviceRef.getService().commit(donated);
        } catch (ServiceUnavailableException serviceUnavailable) {
            LOGGER.warning("Could not get Chargeback service to commit cancelled donation: " + donated, serviceUnavailable);
        } catch (StorageException e) {
            throw new RuntimeException("Could not commit cancelled donation: " + donated, e);
        }
    }

    private static Donated checkIfDonated(Cancellable cancellable) {
        if (cancellable instanceof Donated) {
            return (Donated) cancellable;
        }
        return null;
    }

    @Override
    public void cleanup() {
        serviceRef.cleanup();
    }
}
