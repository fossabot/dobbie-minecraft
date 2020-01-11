package live.dobbie.core.user;

import live.dobbie.core.settings.Settings;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.parser.ISettingsParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleUserSettingsProvider<O extends ISettingsSection> implements UserSettingsProvider {
    private final @NonNull SettingsSourceProvider<O> sourceProvider;
    private final @NonNull ISettingsParser.Provider<O> parserProvider;

    private final Map<User, Settings> settingsMap = new HashMap<>();

    @Override
    public Settings get(@NonNull User user) {
        return settingsMap.get(user);
    }

    @Override
    public boolean refreshValues() {
        boolean anyChanged = false;
        for (Settings value : settingsMap.values()) {
            anyChanged |= value.refreshValues();
        }
        return anyChanged;
    }

    @Override
    public void registerUser(@NonNull User user) throws SettingsSourceNotFoundException {
        if (settingsMap.containsKey(user)) {
            throw new IllegalArgumentException("user already registered in settingsMap: " + user);
        }
        settingsMap.put(user, new Settings(sourceProvider.getSettings(user), parserProvider));
    }

    @Override
    public void unregisterUser(@NonNull User user) {
        Settings settings = settingsMap.remove(user);
        if (settings == null) {
            throw new IllegalArgumentException("user was not registered in settingsMap");
        }
        settings.cleanup();
    }

    @Override
    public void cleanup() {
        settingsMap.values().forEach(Settings::cleanup);
        settingsMap.clear();
    }
}
