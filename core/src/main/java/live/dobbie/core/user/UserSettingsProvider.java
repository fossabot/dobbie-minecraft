package live.dobbie.core.user;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.util.Refreshable;
import lombok.NonNull;

public interface UserSettingsProvider extends UserRegisterListener, Refreshable {
    @NonNull ISettings get(@NonNull User user);

    @Override
    void registerUser(@NonNull User user) throws SettingsSourceNotFoundException;
}
