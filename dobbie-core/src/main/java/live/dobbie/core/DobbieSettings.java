package live.dobbie.core;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.user.UserSettingsProvider;
import live.dobbie.core.util.Refreshable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Getter;
import lombok.NonNull;

public class DobbieSettings implements Refreshable {
    private static final ILogger LOGGER = Logging.getLogger(DobbieSettings.class);

    private final @NonNull
    @Getter
    ISettings globalSettings;
    private final @NonNull
    @Getter
    UserSettingsProvider userSettingsProvider;

    public DobbieSettings(@NonNull ISettings globalSettings,
                          @NonNull UserSettingsProvider userSettingsProvider) {
        this.globalSettings = globalSettings;
        this.userSettingsProvider = userSettingsProvider;
    }

    @Override
    public void refresh() {
        globalSettings.refresh();
        userSettingsProvider.refresh();
    }
}
