package live.dobbie.core.user;

import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.source.ISettingsSectionSource;
import lombok.NonNull;

public interface SettingsSourceProvider<O extends ISettingsSection> {
    @NonNull ISettingsSectionSource<O> getSettings(@NonNull User user) throws SettingsSourceNotFoundException;
}
