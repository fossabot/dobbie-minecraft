package live.dobbie.core.script.js;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.misc.primitive.storage.MutablePrimitiveStorage;
import live.dobbie.core.misc.primitive.storage.PrimitiveMap;
import live.dobbie.core.misc.primitive.storage.StorageAwareObjectContext;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptResult;
import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.script.js.converter.*;
import live.dobbie.core.script.js.moduleprovider.JSModuleProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JSScriptContextTest {

    @Test
    void noNewKeysTraverseTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cf = new ContextFactory();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        JSScriptContext context = new JSScriptContext(
                cf,
                null,
                new StorageAwareObjectContext(
                        SimpleContext.builder()
                                .set(Path.of("foo"), Primitive.of("bar"))
                                .build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        assertEquals("bar", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo", "test")),
                context
        ));
        executor.execute(
                compiler.compile(ScriptSource.fromString("vars.foo = 'hey!'", "test")),
                context
        );
        assertEquals("hey!", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo", "test")),
                context
        ));
    }

    @Test
    void noPredefinedVarsTraverseTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cf = new ContextFactory();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        JSScriptContext context = new JSScriptContext(
                cf,
                null,
                new StorageAwareObjectContext(
                        SimpleContext.builder().build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        executor.execute(
                compiler.compile(ScriptSource.fromString("vars.foo = 'hey!'", "test")),
                context
        );
        assertEquals("hey!", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo", "test")),
                context
        ));
    }

    @Test
    void newKeysTraverseTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cf = new ContextFactory();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        JSScriptContext context = new JSScriptContext(
                cf,
                null,
                new StorageAwareObjectContext(
                        SimpleContext.builder()
                                .set(Path.of("foo"), Primitive.of("bar"))
                                .build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .registerFromConverter(Object.class, new AccessorJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScript script;
        ScriptResult result;

        script = compiler.compile(ScriptSource.fromString("vars.displayName", "test"));
        result = executor.execute(script, context);
        assertTrue(result.isNull());

        script = compiler.compile(ScriptSource.fromString("vars.displayName = 'hey!'", "test"));
        executor.execute(script, context);

        script = compiler.compile(ScriptSource.fromString("vars.displayName", "test"));
        result = executor.execute(script, context);
        assertEquals("hey!", result.getObject());
    }

    @Test
    void newNestedKeysTraverseTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cf = new ContextFactory();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        JSScriptContext context = new JSScriptContext(
                cf,
                null,
                new StorageAwareObjectContext(
                        SimpleContext.builder().build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        executor.execute(
                compiler.compile(ScriptSource.fromString("vars.foo.bar = 'hey!'", "test")),
                context
        );
        assertEquals("hey!", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo.bar", "test")),
                context
        ));
    }

    @Test
    void nestedCombinedKeysTraverseTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cf = new ContextFactory();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        storage.setVariable(Path.of("foo", "bar"), Primitive.of("hello"));
        storage.setVariable(Path.of("foo", "bar", "hello"), Primitive.of("world"));
        JSScriptContext context = new JSScriptContext(
                cf,
                null,
                new StorageAwareObjectContext(
                        SimpleContext.builder().build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        executor.execute(
                compiler.compile(ScriptSource.fromString("vars.foo.bar = 'hey!'", "test")),
                context
        );
        assertEquals("hey!", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo.bar", "test")),
                context
        ));
        assertEquals("world", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.foo.bar.hello", "test")),
                context
        ));
    }

    @Test
    void moduleLoaderTest() throws IOException, ScriptCompilationException, ScriptExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSModuleProvider moduleProvider = Mockito.mock(JSModuleProvider.class);
        when(moduleProvider.getModuleSource(eq("test"), any(), any())).thenReturn(
                ScriptSource.fromString("exports.foo = function(){ return 'bar'; }", "module")
        );
        JSModuleScriptProvider moduleScriptProvider = new JSModuleScriptProvider(moduleProvider, compiler);
        JSScriptContext context = new JSScriptContext(cf, moduleScriptProvider, SimpleContext.builder().build());
        JSScriptExecutor executor = new JSScriptExecutor();

        assertEquals(
                "bar",
                executor.executeString(
                        compiler.compile(
                                ScriptSource.fromString("require('test').foo()", "test")
                        ),
                        context
                )
        );
    }

}