package live.dobbie.core.context.value;

import com.fasterxml.jackson.annotation.JsonCreator;
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
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ContextualConditionTest {

    @Test
    void jsParserTest() throws ComputationException, IOException {
        ObjectContext context = Mockito.mock(ObjectContext.class);
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualCondition.class, newParser());
        o.registerModule(module);
        ContextualCondition conditionObject;

        conditionObject = o.readValue("\"true\"", ContextualCondition.class);
        assertNotNull(conditionObject);
        assertTrue(conditionObject.isTrue(context));

        conditionObject = o.readValue("\"false\"", ContextualCondition.class);
        assertNotNull(conditionObject);
        assertFalse(conditionObject.isTrue(context));
    }

    @Test
    void varParserTest() throws ComputationException, IOException {
        ObjectContext context = SimpleContext.builder()
                .set(Path.of("foo"), Primitive.of("bar"))
                //.set(Path.of("num"), Primitive.of(5.0))
                .build();
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualCondition.class, newParser());
        o.registerModule(module);
        ContextualCondition conditionObject;

        conditionObject = o.readValue("{\"foo\": \"bar\"}", ContextualCondition.class);
        assertNotNull(conditionObject);
        assertTrue(conditionObject.isTrue(context));

        conditionObject = o.readValue("{\"foo\": \"b0r\"}", ContextualCondition.class);
        assertNotNull(conditionObject);
        assertFalse(conditionObject.isTrue(context));

        ContextualCondition throwingConditionObject = o.readValue("{\"foo\": \"bar\", \"f00\": \"non-existent\"}", ContextualCondition.class);
        assertNotNull(conditionObject);
        assertThrows(IllegalArgumentException.class, () -> throwingConditionObject.isTrue(context));
    }

    @Test
    void combinedParserTest() throws ComputationException, IOException {
        ObjectContext context = SimpleContext.builder()
                .set(Path.of("foo"), Primitive.of("bar"))
                .set(Path.of("num"), Primitive.of(5.0))
                .build();
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualCondition.class, newParser());
        o.registerModule(module);
        ConditionTest test;

        test = o.readValue("{\"c0\": \"foo == 'bar'\", \"c1\": {\"foo\": \"bar\"}}", ConditionTest.class);
        assertNotNull(test);
        assertTrue(test.c0.isTrue(context));
        assertTrue(test.c1.isTrue(context));

        test = o.readValue("{\"c0\": \"num == 5.0\", \"c1\": {\"num\": 5.0}}", ConditionTest.class);
        assertNotNull(test);
        assertTrue(test.c0.isTrue(context));
        assertTrue(test.c1.isTrue(context));
    }

    @Test
    void parseAnyCondition() throws IOException, ComputationException {
        ObjectContext context = SimpleContext.builder().build();
        ObjectMapper o = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ContextualCondition.class, newParser());
        o.registerModule(module);

        ContextualCondition c0 = o.readValue("\"any\"", ContextualCondition.class);
        assertNotNull(c0);
        assertTrue(c0.isTrue(context));
    }

    private ContextualCondition.Parser newParser() {
        ContextFactory jsCtxFactory = new ContextFactory();
        ScriptContextualValue.Factory<JSScript, JSScriptContext> scCtxFactory = new ScriptContextualValue.Factory<>(
                new JSScriptContext.Factory(jsCtxFactory, TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build()
                ),
                new JSScriptExecutor(),
                new JSScriptCompiler(jsCtxFactory)
        );
        return new ContextualCondition.Parser(scCtxFactory, "test");
    }

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    public static class ConditionTest {
        @NonNull ContextualCondition c0;
        @NonNull ContextualCondition c1;
    }
}