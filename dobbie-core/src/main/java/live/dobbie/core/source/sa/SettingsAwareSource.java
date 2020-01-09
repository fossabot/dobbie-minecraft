package live.dobbie.core.source.sa;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.source.Source;
import live.dobbie.core.user.User;
import lombok.NonNull;


public abstract class SettingsAwareSource<V extends ISettingsValue> extends Source.UsingQueue {
    protected final @NonNull ISettings settings;
    private final SettingsSubscription<V> subscription;

    public SettingsAwareSource(@NonNull User user, @NonNull ISettings settings, @NonNull Class<V> subscriptionKey) {
        super(user);
        this.settings = settings;
        this.subscription = this.settings.registerListener(subscriptionKey, this::onValueUpdated, false);
    }

    protected final void init() {
        onValueUpdated(subscription.getValue());
    }

    protected abstract void onValueUpdated(V newValue);
}
