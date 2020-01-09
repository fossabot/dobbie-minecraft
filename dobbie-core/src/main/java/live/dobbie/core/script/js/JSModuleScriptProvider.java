package live.dobbie.core.script.js;

import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.script.js.moduleprovider.JSModuleProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

import java.net.URI;

@RequiredArgsConstructor
public class JSModuleScriptProvider implements ModuleScriptProvider {
    private final @NonNull JSModuleProvider provider;
    private final @NonNull JSScriptCompiler compiler;

    @Override
    public ModuleScript getModuleScript(Context cx, String moduleId, URI moduleUri,
                                        URI baseUri, Scriptable paths) throws Exception {
        if (paths instanceof NativeArray && !((NativeArray) paths).isEmpty()) {
            throw new Exception("\"paths\" argument passed into require() is not supported");
        }
        ScriptSource moduleSource = provider.getModuleSource(moduleId, moduleUri, baseUri);
        JSScript script = compiler.compile(moduleSource);
        return new ModuleScript(script.getScript(), moduleSource.getUri(), baseUri);
    }
}
