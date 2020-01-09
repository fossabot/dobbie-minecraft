package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.dobbie.core.settings.upgrader.SchemaUpgrader;
import live.dobbie.core.user.SimpleUserSettingsProvider;
import live.dobbie.core.user.User;
import live.dobbie.core.util.io.FileSupplier;
import live.dobbie.core.util.io.IOSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class JacksonSourceProvider implements SimpleUserSettingsProvider.SettingsSourceProvider<JacksonNode> {
    private final @NonNull ObjectMapper objectMapper;
    private final SchemaUpgrader upgrader;
    private final @NonNull UserIOSupplier userIOSupplier;

    @Override
    public JacksonSource getSettings(@NonNull User user) {
        return new JacksonSource(objectMapper, userIOSupplier.getSettingsIOSupplier(user), upgrader);
    }

    public interface UserIOSupplier {
        @NonNull IOSupplier getSettingsIOSupplier(@NonNull User user);
    }

    @RequiredArgsConstructor
    public static class DirectoryUserFileSupplier implements UserIOSupplier {
        private final @NonNull File directory;

        @Override
        public @NonNull FileSupplier getSettingsIOSupplier(@NonNull User user) {
            return new FileSupplier(new File(directory, user.getName() + ".yaml"));
        }
    }
}
