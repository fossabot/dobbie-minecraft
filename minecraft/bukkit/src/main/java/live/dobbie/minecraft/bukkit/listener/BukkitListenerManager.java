package live.dobbie.minecraft.bukkit.listener;

import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BukkitListenerManager implements Cleanable {
    private static final ILogger LOGGER = Logging.getLogger(BukkitListenerManager.class);

    private final Map<Class<? extends Event>, Instance> instances = new HashMap<>();
    private final Plugin plugin;

    public synchronized BukkitListenerCanceller registerListener(@NonNull BukkitListener listener) {
        Instance instance = getInstance(listener.getEventClass());
        return instance.registerListener(listener);
    }

    private Instance getInstance(Class<? extends Event> eventClass) {
        Instance instance = instances.get(eventClass);
        if(instance == null) {
            instance = new Instance(eventClass, plugin);
            instances.put(eventClass, instance);
        }
        return instance;
    }

    @Override
    public synchronized void cleanup() {
        instances.values().forEach(Cleanable::cleanup);
        instances.clear();
    }

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static class Instance implements Cleanable {
        @NonNull Class<? extends Event> eventClass;
        @NonNull Plugin plugin;
        List<BukkitNativeListener> listeners = new ArrayList<>();

        BukkitListenerCanceller registerListener(@NonNull BukkitListener listener) {
            BukkitNativeListener nativeListener = new BukkitNativeListener(listener);
            listeners.add(nativeListener);
            Canceller canceller = new Canceller(this, nativeListener);
            registerInPluginManager(nativeListener, canceller);
            return canceller;
        }

        private void registerInPluginManager(BukkitNativeListener nativeListener, BukkitListenerCanceller canceller) {
            plugin.getServer().getPluginManager().registerEvent(
                    nativeListener.getListener().getEventClass(),
                    nativeListener,
                    nativeListener.getListener().getPriority(),
                    (listener, event) -> nativeListener.getListener().getConsumer().accept(new BukkitEvent(event, canceller)),
                    plugin
            );
        }

        boolean removeNativeListener(@NonNull BukkitNativeListener nativeListener) {
            boolean remove = listeners.remove(nativeListener);
            if(remove) {
                nativeListener.unregister();
            }
            return remove;
        }

        @NonNull
        @Override
        public void cleanup() {
            listeners.clear();
        }
    }
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static class Canceller implements BukkitListenerCanceller {
        @NonNull Instance instance;
        @NonNull BukkitNativeListener nativeListener;

        @Override
        public void cancelListener() {
            instance.removeNativeListener(nativeListener);
        }
    }
}
