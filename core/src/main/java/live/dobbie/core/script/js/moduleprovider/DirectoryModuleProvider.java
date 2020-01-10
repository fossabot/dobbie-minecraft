package live.dobbie.core.script.js.moduleprovider;

import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.util.io.FileSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class DirectoryModuleProvider implements JSModuleProvider {
    private final @NonNull File directory;

    @Override
    public @NonNull ScriptSource getModuleSource(String moduleId, URI moduleUri, URI baseUri) throws IOException {
        File moduleFile = new File(directory, toModuleFileName(moduleId));
        if (!moduleFile.isFile()) {
            throw new FileNotFoundException(moduleFile.getAbsolutePath());
        }
        return new ScriptSource(
                new FileSupplier(moduleFile, false),
                StandardCharsets.UTF_8,
                moduleFile.getName(),
                moduleFile.toURI(),
                0
        );
    }

    private static String toModuleFileName(@NonNull String moduleId) {
        return moduleId + ".js";
    }
}
