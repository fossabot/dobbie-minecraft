package live.dobbie.minecraft.bukkit.listener;

import lombok.NonNull;
import lombok.Value;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Value
class BukkitNativeListener implements Listener {
    @NonNull BukkitListener listener;

    void unregister() {
        HandlerList.unregisterAll(this);
    }
}
