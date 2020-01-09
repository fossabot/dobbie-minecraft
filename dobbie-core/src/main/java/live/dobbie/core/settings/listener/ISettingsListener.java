package live.dobbie.core.settings.listener;

import live.dobbie.core.settings.value.ISettingsValue;


public interface ISettingsListener<V extends ISettingsValue> {
    void onSettingsChanged(V newValue);

    ISettingsListener<?> DUMMY_LISTENER = newValue -> {
    };

    static <V extends ISettingsValue> ISettingsListener<V> dummyListener() {
        return (ISettingsListener<V>) DUMMY_LISTENER;
    }
}
