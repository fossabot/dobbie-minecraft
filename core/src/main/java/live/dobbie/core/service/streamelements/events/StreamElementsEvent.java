package live.dobbie.core.service.streamelements.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreamElementsEvent {
    @NonNull String _id;
    @NonNull String channel;
    @NonNull String type;
}
