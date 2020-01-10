package live.dobbie.core.settings.listener;

import live.dobbie.core.settings.value.ISettingsValue;


public interface SettingsSubscription<V extends ISettingsValue> {
    V getValue();

    void cancelSubscription();
}
