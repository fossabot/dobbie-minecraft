package live.dobbie.core.dest;

import live.dobbie.core.path.Path;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


public interface DestSectionLocator {
    DestSection getSection(@NonNull Path path);

    @NonNull DestSection requireSection(@NonNull Path path) throws IllegalArgumentException;

    interface Factory {
        @NonNull DestSectionLocator create(@NonNull User user);

        @RequiredArgsConstructor
        class UsingSettingsProvider implements Factory {
            private final @NonNull UserSettingsProvider userSettingsProvider;

            @Override
            public @NonNull DestSectionLocator.UsingSettings create(@NonNull User user) {
                return new DestSectionLocator.UsingSettings(userSettingsProvider.get(user));
            }
        }
    }

    @RequiredArgsConstructor
    class UsingSettings implements DestSectionLocator {
        private final @NonNull ISettings settings;

        @Override
        public DestSection getSection(@NonNull Path path) {
            return queryDestMap().getSection(path);
        }

        @Override
        public @NonNull DestSection requireSection(@NonNull Path path) throws IllegalArgumentException {
            return queryDestMap().requireSection(path);
        }

        @NonNull
        private DestMap queryDestMap() {
            return settings.requireValue(DestMap.class);
        }
    }
}
