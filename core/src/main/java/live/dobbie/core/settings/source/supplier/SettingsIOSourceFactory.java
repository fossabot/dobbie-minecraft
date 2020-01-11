package live.dobbie.core.settings.source.supplier;

import live.dobbie.core.user.SettingsSourceNotFoundException;
import live.dobbie.core.user.User;
import live.dobbie.core.util.io.IOSupplier;
import lombok.NonNull;

public interface SettingsIOSourceFactory {
    @NonNull IOSupplier getSettingsIOSupplier(@NonNull User user) throws SettingsSourceNotFoundException;
}
