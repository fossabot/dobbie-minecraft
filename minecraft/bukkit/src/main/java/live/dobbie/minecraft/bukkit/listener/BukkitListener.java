package live.dobbie.minecraft.bukkit.listener;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.function.Consumer;

@EqualsAndHashCode(of = {"eventClass", "priority"})
@Value
public class BukkitListener {
    @NonNull Class<? extends Event> eventClass;
    @NonNull Consumer<BukkitEvent> consumer;
    @NonNull EventPriority priority;
}
