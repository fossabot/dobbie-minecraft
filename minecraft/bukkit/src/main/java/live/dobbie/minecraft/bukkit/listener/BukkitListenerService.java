package live.dobbie.minecraft.bukkit.listener;

import live.dobbie.core.service.Service;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.service.SingleServiceRef;
import live.dobbie.core.user.User;
import live.dobbie.minecraft.bukkit.compat.BukkitPlayer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BukkitListenerService implements Service {
    public static final String NAME = "bukkitListener";
    private final Map<BukkitListener, BukkitListenerCanceller> cancellerMap = new HashMap<>();

    private final @NonNull BukkitListenerManager manager;
    private final @NonNull BukkitPlayer player;

    public BukkitListenerCanceller on(@NonNull Class<? extends Event> eventClass,
                                      @NonNull EventPriority eventPriority,
                                      @NonNull Consumer<BukkitEvent> consumer) {
        BukkitListener bukkitListener = new BukkitListener(eventClass, consumer, eventPriority);
        if(cancellerMap.containsKey(bukkitListener)) {
            BukkitListenerCanceller canceller = cancellerMap.get(bukkitListener);
            canceller.cancelListener();
            cancellerMap.remove(bukkitListener);
        }
        BukkitListenerCanceller canceller = manager.registerListener(bukkitListener);
        cancellerMap.put(bukkitListener, canceller);
        return canceller;
    }

    public BukkitListenerCanceller on(@NonNull Class<? extends Event> eventClass,
                                      @NonNull Consumer<BukkitEvent> consumer) {
        return on(eventClass, EventPriority.NORMAL, consumer);
    }

    public BukkitListenerCanceller on(@NonNull String name, @NonNull String priority, @NonNull Consumer<BukkitEvent> consumer) {
        Class<? extends Event> eventClass = nameToClass(name);
        EventPriority eventPriority = parsePriority(priority);
        return on(eventClass, eventPriority, consumer);
    }

    public BukkitListenerCanceller on(@NonNull String name, @NonNull Consumer<BukkitEvent> consumer) {
        Class<? extends Event> eventClass = nameToClass(name);
        return on(eventClass, consumer);
    }

    public BukkitListenerCanceller onPlayer(@NonNull BukkitPlayer player,
                                            @NonNull Class<? extends Event> eventClass,
                                            @NonNull EventPriority eventPriority,
                                            @NonNull Consumer<BukkitEvent> consumer) {
        checkIfPlayerEvent(eventClass);
        return on(eventClass, eventPriority, new PlayerPrecondition(player, consumer));
    }

    public BukkitListenerCanceller onPlayer(@NonNull BukkitPlayer player,
                                            @NonNull Class<? extends Event> eventClass,
                                            @NonNull Consumer<BukkitEvent> consumer) {
        checkIfPlayerEvent(eventClass);
        return on(eventClass, new PlayerPrecondition(player, consumer));
    }

    public BukkitListenerCanceller onPlayer(@NonNull BukkitPlayer player,
                                            @NonNull String name,
                                            @NonNull String priority,
                                            @NonNull Consumer<BukkitEvent> consumer) {
        Class<? extends Event> eventClass = nameToClass(name);
        checkIfPlayerEvent(eventClass);
        EventPriority eventPriority = parsePriority(priority);
        return on(eventClass, eventPriority, new PlayerPrecondition(player, consumer));
    }

    public BukkitListenerCanceller onPlayer(@NonNull BukkitPlayer player,
                                            @NonNull String name,
                                            @NonNull Consumer<BukkitEvent> consumer) {
        Class<? extends Event> eventClass = nameToClass(name);
        checkIfPlayerEvent(eventClass);
        return on(eventClass, new PlayerPrecondition(player, consumer));
    }

    public BukkitListenerCanceller onMe(@NonNull Class<? extends Event> eventClass,
                                        @NonNull EventPriority eventPriority,
                                        @NonNull Consumer<BukkitEvent> consumer) {
        return onPlayer(this.player, eventClass, eventPriority, consumer);
    }

    public BukkitListenerCanceller onMe(@NonNull Class<? extends Event> eventClass,
                                        @NonNull Consumer<BukkitEvent> consumer) {
        return onPlayer(this.player, eventClass, consumer);
    }

    public BukkitListenerCanceller onMe(@NonNull String name,
                                        @NonNull String priority,
                                        @NonNull Consumer<BukkitEvent> consumer) {
        return onPlayer(this.player, name, priority, consumer);
    }

    public BukkitListenerCanceller onMe(@NonNull String name,
                                        @NonNull Consumer<BukkitEvent> consumer) {
        return onPlayer(this.player, name, consumer);
    }

    public boolean unregister(@NonNull Class<? extends Event> eventClass) {
        return cancellerMap.entrySet().removeIf(entry -> {
            if(entry.getKey().getEventClass().equals(eventClass)) {
                entry.getValue().cancelListener();
                return true;
            }
            return false;
        });
    }

    @Override
    public void cleanup() {
        cancellerMap.values().forEach(BukkitListenerCanceller::cancelListener);
        cancellerMap.clear();
    }

    @NonNull
    private static Class<? extends Event> nameToClass(@NonNull String name) {
        Class<?> eventClass;
        try {
            eventClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("could not find event by name: \""+ name +"\"", e);
        }
        if(!Event.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException(eventClass + " is not derived from " + Event.class);
        }
        return (Class<? extends Event>) eventClass;
    }

    @NonNull
    private static EventPriority parsePriority(@NonNull String value) {
        return EventPriority.valueOf(value.toUpperCase());
    }

    private static void checkIfPlayerEvent(Class<? extends Event> eventClass) {
        if(!PlayerEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException(eventClass + " is not assignable from " + PlayerEvent.class);
        }
    }

    @RequiredArgsConstructor
    public static class RefFactory implements ServiceRef.Factory<BukkitListenerService> {
        private final @NonNull BukkitListenerManager manager;

        @Override
        public @NonNull ServiceRef<BukkitListenerService> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user) {
            return new SingleServiceRef<>(NAME, new BukkitListenerService(manager, (BukkitPlayer) user), provider);
        }
    }

    private static class PlayerPrecondition implements Consumer<BukkitEvent> {
        private final @NonNull UUID nativePlayerUniqueId;
        private final @NonNull Consumer<BukkitEvent> delegate;

        public PlayerPrecondition(@NonNull BukkitPlayer bukkitPlayer,
                                  @NonNull Consumer<BukkitEvent> delegate) {
            this.nativePlayerUniqueId = bukkitPlayer.getNativePlayer().getUniqueId();
            this.delegate = delegate;
        }

        @Override
        public void accept(BukkitEvent bukkitEvent) {
            Event event = bukkitEvent.getEvent();
            if(event instanceof PlayerEvent) {
                if(((PlayerEvent) event).getPlayer().getUniqueId().equals(nativePlayerUniqueId)) {
                    delegate.accept(bukkitEvent);
                }
            }
        }
    }
}
