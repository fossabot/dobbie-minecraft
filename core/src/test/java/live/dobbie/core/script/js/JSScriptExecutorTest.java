package live.dobbie.core.script.js;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.storage.MutablePrimitiveStorage;
import live.dobbie.core.context.storage.PrimitiveMap;
import live.dobbie.core.context.storage.StorageAwareObjectContext;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptExecutionException;
import live.dobbie.core.script.ScriptResult;
import live.dobbie.core.script.ScriptSource;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JSScriptExecutorTest {

    @Test
    void basicTest() throws IOException, ScriptCompilationException, ScriptExecutionException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext context = new JSScriptContext(cxf, null, new SimpleContext(), DefaultValueConverter.INSTANCE);

        JSScript script = compiler.compile(ScriptSource.fromString("'hello'", "test"));
        ScriptResult result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("hello", result.getObject());
    }

    @Test
    void compileFailTest() throws IOException, ScriptCompilationException, ScriptExecutionException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        Assertions.assertThrows(ScriptCompilationException.class, () ->
                compiler.compile(ScriptSource.fromString("@$#%", "test"))
        );
    }

    @Test
    void execFailTest() throws IOException, ScriptCompilationException, ScriptExecutionException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext context = new JSScriptContext(cxf, null, new SimpleContext(), DefaultValueConverter.INSTANCE);

        JSScript script = compiler.compile(ScriptSource.fromString("foo // -> foo is not defined", "test"));
        Assertions.assertThrows(ScriptExecutionException.class, () -> executor.execute(script, context));
    }

    @Test
    void conversionTest() throws IOException, ScriptCompilationException, ScriptExecutionException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext context = new JSScriptContext(cxf, null, new SimpleContext(), DefaultValueConverter.INSTANCE);
        JSScript script = compiler.compile(ScriptSource.fromString("3 + 3", "test"));

        assertEquals(6, executor.executeInteger(script, context));
        assertEquals(6.0, executor.executeDouble(script, context));
        assertEquals(6L, executor.executeLong(script, context));

        script = compiler.compile(ScriptSource.fromString("3 + 0.5", "test"));
        assertEquals(3, executor.executeInteger(script, context));
        assertEquals(3.5, executor.executeDouble(script, context));

        // https://i.redd.it/rz3o1yibnc511.png
        script = compiler.compile(ScriptSource.fromString("[] == 0", "test"));
        assertTrue(executor.executeBoolean(script, context));
    }

    @Test
    void varTest() throws IOException, ScriptCompilationException, ScriptExecutionException, InterruptedException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set(Path.of("foo", "bar"), Primitive.of("hello"))
                        .set(Path.of("foo", "tar"), Primitive.of("yello"))
                        .set(Path.of("foo", "bool"), Primitive.of(true))
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script;
        ScriptResult result;

        script = compiler.compile(ScriptSource.fromString("foo.bar", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("hello", result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.tar", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("yello", result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.bar + foo.tar", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("helloyello", result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.bool", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertTrue((Boolean) result.getObject());
    }

    @Test
    void objectTest() throws IOException, ScriptCompilationException, ScriptExecutionException, InterruptedException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        Foo foo = Mockito.spy(new Foo());
        JSScriptContext context = new JSScriptContext(cxf, null, SimpleContext.builder().set("foo", foo).build(), DefaultValueConverter.INSTANCE);
        JSScript script;
        ScriptResult result;

        script = compiler.compile(ScriptSource.fromString("foo.stringTest", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("123", result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.intTest", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        // converted to double!
        assertEquals(123.0, result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.doubleTest", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals(0.5, result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.nullTest", "test"));
        result = executor.execute(script, context);
        assertTrue(result.isNull());
        assertNull(result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.methodTest()", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertTrue((Boolean) result.getObject());

        script = compiler.compile(ScriptSource.fromString("foo.argumentMethodTest('foo')", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertEquals("foo!", result.getObject());
    }

    @Test
    void dateTimeTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        Instant now = Instant.now();
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext context = new JSScriptContext(cxf, null,
                SimpleContext.builder()
                        .set("time", Primitive.of(now))
                        .build(),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build());
        JSScript script;
        ScriptResult result;

        script = compiler.compile(ScriptSource.fromString("time.getTime()", "test"));
        result = executor.execute(script, context);
        assertFalse(result.isNull());
        assertTrue(result.getObject() instanceof Double);
        assertEquals((double) now.toEpochMilli(), (double) result.getObject(), .5);
    }

    @Test
    void setValueTest() throws ScriptCompilationException, ScriptExecutionException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        JSScriptExecutor executor = new JSScriptExecutor();
        MutablePrimitiveStorage storage = new PrimitiveMap();
        JSScriptContext context = new JSScriptContext(cxf, null,
                new StorageAwareObjectContext(
                        SimpleContext.builder().set(Path.of("foo"), Primitive.of("bar")).build(),
                        storage,
                        "varStorage"
                ),
                Path.of("vars"),
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
        );
        JSScript script;

        script = compiler.compile(ScriptSource.fromString("vars.foo = 'hello!'", "test"));
        executor.execute(script, context);
        assertEquals(Primitive.of("hello!"), storage.getVariable(Path.of("foo")));
    }

    public static class Foo {
        public final String stringTest = "123";
        public final int intTest = 123;
        public final double doubleTest = 0.5;
        public final Object nullTest = null;

        public final boolean methodTest() {
            return true;
        }

        public final String argumentMethodTest(String argument) {
            return argument + "!";
        }
    }
}