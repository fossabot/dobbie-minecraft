package live.dobbie.core.script.js;

import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptExecutor;
import live.dobbie.core.script.ScriptResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class JSScriptExecutor implements ScriptExecutor<JSScript, JSScriptContext> {
    private final @NonNull Collection<JSScript> polyfillScripts;

    public JSScriptExecutor() {
        this(Collections.emptySet());
    }

    @Override
    @NonNull
    public <T> ScriptResult<T> execute(@NonNull JSScript scriptObject, @NonNull JSScriptContext scriptContext, @NonNull Class<T> expectedType) throws ScriptExecutionException {
        T result;
        try {
            result = scriptContext.executeScript(polyfillScripts, scriptObject, expectedType);
        } catch (Exception e) {
            throw new ScriptExecutionException(e);
        }
        return new ScriptResult<>(result);
    }
}
