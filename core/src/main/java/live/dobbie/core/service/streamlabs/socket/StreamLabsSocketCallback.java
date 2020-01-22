package live.dobbie.core.service.streamlabs.socket;

import live.dobbie.core.service.streamlabs.socket.event.StreamLabsEvent;
import lombok.NonNull;

public interface StreamLabsSocketCallback {
    void proceedEvent(@NonNull StreamLabsEvent event);
}
