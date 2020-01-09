package live.dobbie.core.context.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.JSScript;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContextualValueTest {

    @Test
    void basicTest() throws IOException, ComputationException {
        ObjectContext context = Mockito.mock(ObjectContext.class);
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualValue.class, newParser());
        o.registerModule(module);
        ContextualValue value;

        value = o.readValue("\"3 + 3\"", ContextualValue.class);
        assertNotNull(value);
        assertEquals(6.0, value.computeValue(context));
    }

    @Test
    void varExtractTest() throws IOException, ComputationException {
        ObjectContext context = SimpleContext.builder().set(Path.of("foo0"), Primitive.of("hello")).set("foo1", ", world!").build();
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualValue.class, newParser());
        o.registerModule(module);
        ContextualValue value;

        value = o.readValue("\"foo0 + foo1\"", ContextualValue.class);
        assertNotNull(value);
        assertEquals("hello, world!", value.computeValue(context));
    }

    private ContextualValue.Parser newParser() {
        ContextFactory jsCtxFactory = new ContextFactory();
        ScriptContextualValue.Factory<JSScript, JSScriptContext> scCtxFactory = new ScriptContextualValue.Factory<>(
                new JSScriptContext.Factory(jsCtxFactory, TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()),
                new JSScriptExecutor(),
                new JSScriptCompiler(jsCtxFactory)
        );
        return new ContextualValue.Parser(new ScriptContextualValue.Parser<>(scCtxFactory, "test", Object.class));
    }

}