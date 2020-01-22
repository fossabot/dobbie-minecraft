package live.dobbie.core.script.js;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.storage.MutablePrimitiveStorage;
import live.dobbie.core.context.storage.PrimitiveStorage;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptResult;
import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.PrimitiveStorageJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PSAdapterTest {

    @Test
    void getTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        PrimitiveStorage storage = mock(PrimitiveStorage.class);
        when(storage.getVariable(Path.of("hello"))).thenReturn(Primitive.of("world"));
        PrimitiveJSConverter primitiveJSConverter = new PrimitiveJSConverter(DefaultValueConverter.INSTANCE);
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set("storage", storage)
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(primitiveJSConverter)
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script = compiler.compile(ScriptSource.fromString("storage.get('hello')", "test"));
        ScriptResult result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("world", result.getObject());
    }

    @Test
    void setTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        PrimitiveJSConverter primitiveJSConverter = new PrimitiveJSConverter(DefaultValueConverter.INSTANCE);
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set("storage", storage)
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(primitiveJSConverter)
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script = compiler.compile(ScriptSource.fromString("storage.set('hello', 'world')", "test"));
        ScriptResult result = executor.execute(script, context);
        assertTrue(result.isNull());
        verify(storage).setVariable(eq(Path.of("hello")), eq(Primitive.of("world")));
    }

    @Test
    void removeTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        PrimitiveJSConverter primitiveJSConverter = new PrimitiveJSConverter(DefaultValueConverter.INSTANCE);
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set("storage", storage)
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(primitiveJSConverter)
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script = compiler.compile(ScriptSource.fromString("storage.remove('hello')", "test"));
        ScriptResult result = executor.execute(script, context);
        assertTrue(result.isNull());
        verify(storage).removeVariable(eq(Path.of("hello")));
    }

    @Test
    void hasTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        when(storage.hasVariable(eq(Path.of("hello")))).thenReturn(true);
        PrimitiveJSConverter primitiveJSConverter = new PrimitiveJSConverter(DefaultValueConverter.INSTANCE);
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set("storage", storage)
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(primitiveJSConverter)
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script = compiler.compile(ScriptSource.fromString("storage.has('hello')", "test"));
        ScriptResult result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals(Boolean.TRUE, result.getObject());
    }

}