package live.dobbie.core.script.js.moduleprovider;

import live.dobbie.core.script.ScriptSource;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;

public interface JSModuleProvider {
    @NonNull ScriptSource getModuleSource(String moduleId, URI moduleUri, URI baseUri) throws IOException;
}
