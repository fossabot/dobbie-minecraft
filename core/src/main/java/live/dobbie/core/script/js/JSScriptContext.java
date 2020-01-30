package live.dobbie.core.script.js;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.script.ScriptContext;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.JSValueConverter;
import live.dobbie.core.script.js.primitivestorage.accessor.PSAccessorFactory;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

import java.util.Collection;
// import org.mozilla.javascript.tools.debugger.Main;

@RequiredArgsConstructor
@Getter
public class JSScriptContext implements ScriptContext {
    private static final ILogger LOGGER = Logging.getLogger(JSScriptExecutor.class);

    private final @NonNull ContextFactory factory;
    private final JSModuleScriptProvider moduleProvider;
    private final @NonNull ObjectContext objectContext;
    private final @NonNull JSValueConverter valueConverter;

    public JSScriptContext(@NonNull ContextFactory factory,
                           JSModuleScriptProvider moduleProvider,
                           @NonNull ObjectContext objectContext) {
        this(factory, moduleProvider, objectContext, DefaultValueConverter.INSTANCE);
    }

    // private final Main debugger; // currently not working


    <T> T executeScript(@NonNull Collection<JSScript> polyfillScriptObjects, @NonNull JSScript scriptObject, Class<T> convertToType) throws IllegalArgumentException, IllegalStateException, RhinoException {
        /*if(debugger != null) {
            debugger.attachTo(factory);
        }*/
        Context context = factory.enterContext();
        try {
            Scriptable scope = setupContext(context);
            for (JSScript polyfillScripts : polyfillScriptObjects) {
                polyfillScripts.getScript().exec(context, scope);
            }
            Object result = scriptObject.getScript().exec(context, scope);
            if (convertToType != null) {
                if (result instanceof Undefined) {
                    return null;
                }
                return valueConverter.fromJs(result, convertToType);
            }
            return (T) result;
        } finally {
            Context.exit();
        }
    }

    private Scriptable setupContext(Context context) {
        context.setLanguageVersion(Context.VERSION_ES6);
        context.setErrorReporter(new ErrorReporter() {
            @Override
            public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
                LOGGER.warning(message + describeSource(sourceName, line, lineSource, lineOffset));
            }

            @Override
            public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
                LOGGER.error(message + describeSource(sourceName, line, lineSource, lineOffset));
            }

            @Override
            public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
                throw new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
            }
        });
        Scriptable scope = context.initStandardObjects();
        setupModuleProvider(context, scope);
        setupObjectsAndVariables(objectContext, context, scope);
        //setupDebugger(scope);
        return scope;
    }

    private void setupModuleProvider(Context context, Scriptable scope) {
        if (moduleProvider == null) {
            return;
        }
        new RequireBuilder().setSandboxed(false)
                .setModuleScriptProvider(moduleProvider)
                .createRequire(context, scope)
                .install(scope);
    }

    private void setupObjectsAndVariables(ObjectContext objectContext, Context context, Scriptable scope) {
        final PSAccessorFactory psAccessorFactory = new PSAccessorFactory(
                objectContext,
                valueConverter,
                scope,
                context
        );
        objectContext.getObjects().forEach((key, value) -> {
            ScriptableObject.putProperty(scope, key, valueConverter.toJs(value, scope, context));
        });
    }

    private static String describeSource(String sourceName, int line, String lineSource, int lineOffset) {
        return " in [" + sourceName + "; line " + line + "; offset " + lineOffset + "]";
    }

    @RequiredArgsConstructor
    public static class Factory implements ScriptContext.Factory<JSScriptContext> {
        private final @NonNull ContextFactory contextFactory;
        private final @NonNull JSValueConverter valueConverter;
        private final JSModuleScriptProvider moduleProvider;

        public Factory(@NonNull ContextFactory contextFactory, @NonNull JSValueConverter valueConverter) {
            this(contextFactory, valueConverter, null);
        }

        public Factory(@NonNull ContextFactory contextFactory) {
            this(contextFactory, DefaultValueConverter.INSTANCE);
        }

        @NonNull
        @Override
        public JSScriptContext create(@NonNull ObjectContext context) {
            return new JSScriptContext(contextFactory, moduleProvider, context, valueConverter);
        }
    }
}
