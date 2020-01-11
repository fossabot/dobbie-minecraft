package live.dobbie.core.settings.source.supplier;

import live.dobbie.core.user.SettingsSourceNotFoundException;
import live.dobbie.core.user.User;
import live.dobbie.core.util.io.FileSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class DirectoryBasedFileSourceFactory implements SettingsIOSourceFactory {
    private final @NonNull File directory;
    private final @NonNull String extension;

    @Override
    public @NonNull FileSupplier getSettingsIOSupplier(@NonNull User user) throws SettingsSourceNotFoundException {
        File file = new File(directory, user.getName() + extension);
        if (!file.isFile()) {
            throw new SettingsSourceNotFoundException("file not found: " + file.getAbsolutePath());
        }
        return new FileSupplier(file, false);
    }
}
