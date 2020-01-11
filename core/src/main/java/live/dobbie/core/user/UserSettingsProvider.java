package live.dobbie.core.user;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.ISettingsRefreshable;
import lombok.NonNull;

public interface UserSettingsProvider extends UserRegisterListener, ISettingsRefreshable {
    @NonNull ISettings get(@NonNull User user);

    @Override
    void registerUser(@NonNull User user) throws SettingsSourceNotFoundException;
}
