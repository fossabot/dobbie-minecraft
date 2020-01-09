package live.dobbie.core.script.js.moduleprovider;

import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.util.io.URLSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class URIAwareModuleProvider implements JSModuleProvider {
    private final @NonNull JSModuleProvider delegateModuleProvider;

    @Override
    public @NonNull ScriptSource getModuleSource(String moduleId, URI moduleUri, URI baseUri) throws IOException {
        if (moduleUri != null) {
            return resolveUri(moduleUri, moduleId);
        }
        return delegateModuleProvider.getModuleSource(moduleId, null, baseUri);
    }

    private static ScriptSource resolveUri(URI moduleUri, String name) throws MalformedURLException {
        return new ScriptSource(
                new URLSupplier(moduleUri.toURL()),
                StandardCharsets.UTF_8,
                name,
                moduleUri,
                0
        );
    }
}
