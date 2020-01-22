package live.dobbie.core.service.streamelements.socket;

import live.dobbie.core.service.streamelements.events.StreamElementsEvent;
import lombok.NonNull;

public interface StreamElementSocketCallback {
    void proceedEvent(@NonNull StreamElementsEvent event);
}
