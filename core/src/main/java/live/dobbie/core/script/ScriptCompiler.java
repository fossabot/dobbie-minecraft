package live.dobbie.core.script;

import lombok.NonNull;

import java.io.IOException;

public interface ScriptCompiler<S extends Script> {
    @NonNull S compile(@NonNull ScriptSource source) throws IOException, ScriptCompilationException;
}
