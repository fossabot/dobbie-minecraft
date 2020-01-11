package live.dobbie.core.settings;

import live.dobbie.core.settings.listener.ISettingsListener;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;


public interface ISettings extends ISettingsRefreshable {

    <V extends ISettingsValue> V getValue(@NonNull Class<V> key);

    @NonNull <V extends ISettingsValue> SettingsSubscription<V> registerListener(@NonNull Class<V> key, @NonNull ISettingsListener<V> listener, boolean callListenerAfterwards);

    @NonNull
    default <V extends ISettingsValue> SettingsSubscription<V> registerListener(@NonNull Class<V> key, @NonNull ISettingsListener<V> listener) {
        return registerListener(key, listener, true);
    }

    default <V extends ISettingsValue> SettingsSubscription<V> subscribe(@NonNull Class<V> key) {
        return registerListener(key, ISettingsListener.dummyListener());
    }

    @NonNull
    default <V extends ISettingsValue> V requireValue(@NonNull Class<V> key) {
        return Validate.notNull(getValue(key), "no value for " + key);
    }
}
