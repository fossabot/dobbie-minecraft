package live.dobbie.minecraft.bukkit.listener;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Event;

@Value
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class BukkitEvent implements BukkitListenerCanceller {
    @NonNull Event event;
    private final @NonNull BukkitListenerCanceller canceller;

    @Override
    public void cancelListener() {
        canceller.cancelListener();
    }
}
