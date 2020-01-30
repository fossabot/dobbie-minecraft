package live.dobbie.core.script.js;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.storage.MutablePrimitiveStorage;
import live.dobbie.core.context.storage.PrimitiveMap;
import live.dobbie.core.context.storage.StorageAwareObjectContext;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.PrimitiveStorageJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSScriptContextTest {

    @Test
    void simpleVarStorageTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
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
                        "vars"
                ),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        JSScriptExecutor executor = new JSScriptExecutor();
        assertTrue(executor.executeBoolean(
                compiler.compile(ScriptSource.fromString("vars.has('foo')", "test")),
                context
        ));
        assertEquals("bar", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.get('foo')", "test")),
                context
        ));
        executor.execute(
                compiler.compile(ScriptSource.fromString("vars.set('foo', 'hey!')", "test")),
                context
        );
        assertEquals("hey!", executor.executeString(
                compiler.compile(ScriptSource.fromString("vars.get('foo')", "test")),
                context
        ));
    }

}