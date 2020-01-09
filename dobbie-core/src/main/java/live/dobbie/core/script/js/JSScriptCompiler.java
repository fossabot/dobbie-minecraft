package live.dobbie.core.script.js;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptCompiler;
import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class JSScriptCompiler implements ScriptCompiler<JSScript> {
    private static final ILogger LOGGER = Logging.getLogger(JSScriptCompiler.class);

    private final @NonNull ContextFactory contextFactory;

    private final LoadingCache<ScriptSource, JSScript> scriptCache = CacheBuilder.newBuilder()
            .softValues()
            .build(new CacheLoader<ScriptSource, JSScript>() {
                @Override
                public JSScript load(@NonNull ScriptSource source) throws Exception {
                    return explicitCompile(source);
                }
            });
    private Collection<JSScript> polyfills;

    public JSScriptCompiler(@NonNull ContextFactory contextFactory, boolean precompilePolyfills) {
        this.contextFactory = contextFactory;
        if (precompilePolyfills) {
            precompilePolyfills();
        }
    }

    public JSScriptCompiler(@NonNull ContextFactory contextFactory) {
        this(contextFactory, false);
    }

    public Collection<JSScript> getPolyfills() {
        if (polyfills == null) {
            precompilePolyfills();
        }
        return polyfills;
    }

    @NonNull
    @Override
    public JSScript compile(@NonNull ScriptSource source) throws IOException, ScriptCompilationException {
        try {
            return scriptCache.get(source);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof ScriptCompilationException) {
                throw (ScriptCompilationException) cause;
            }
            throw new RuntimeException(e);
        }
    }

    private JSScript explicitCompile(@NonNull ScriptSource source) throws IOException, ScriptCompilationException {
        LOGGER.debug("Compiling " + source);
        Context context = contextFactory.enterContext();
        context.setLanguageVersion(Context.VERSION_ES6);
        ModSignal modSignal = source.getInputSupplier().getModSignal();
        Script script;
        try {
            script = context.compileReader(source.newReader(), source.getSourceName(), source.getSourceLine(), null);
        } catch (RuntimeException rE) {
            throw new ScriptCompilationException("could not compile from source " + source, rE);
        } finally {
            Context.exit();
        }
        return new JSScript(modSignal, script);
    }

    private void precompilePolyfills() {
        Collection<JSScript> polyfills;
        try {
            polyfills = explicitCompilePolyfills();
        } catch (IOException | ScriptCompilationException e) {
            throw new Error("could not compile polyfills");
        }
        this.polyfills = polyfills;
    }

    @NonNull
    private Collection<JSScript> explicitCompilePolyfills() throws IOException, ScriptCompilationException {
        return Collections.singleton(
                compile(ScriptSource.fromURL(getClass().getResource("polyfill.js"), StandardCharsets.UTF_8))
        );
    }
}
