package live.dobbie.core.dest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.context.value.ScriptContextualValue;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import live.dobbie.core.substitutor.environment.Env;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DestMapParserTest {

    @Test
    void test() throws IOException, CmdExecutionException, ComputationException {
        ObjectMapper o = new ObjectMapper(new YAMLFactory());
        SimpleModule m = new SimpleModule();
        ContextFactory cf = new ContextFactory();
        m.addDeserializer(ContextualCondition.class, new ContextualCondition.Parser(
                new ScriptContextualValue.Factory<>(
                        new JSScriptContext.Factory(cf, TypedValueConverter.builder()
                                .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                                .setFallbackConverter(DefaultValueConverter.INSTANCE)
                                .build()),
                        new JSScriptExecutor(),
                        new JSScriptCompiler(cf)
                ),
                "destinations.yml"
        ));
        PlainCmd.Executor executor = Mockito.mock(PlainCmd.Executor.class);
        CmdContext cmdContext = new CmdContext(
                SimpleContext.builder()
                        .set(Path.of("donation", "amount"), Primitive.of("RUB43"))
                        .build(),
                executor,
                Mockito.mock(Env.class)
        );
        m.addDeserializer(Cmd.class, new Cmd.JacksonParser(new SequentalCmdParser(new PlainCmd.Parser())));
        o.registerModule(m);
        DestMap destMap = o.readValue(DestMapParserTest.class.getResource("destinations.yml"), DestMap.class);
        assertNotNull(destMap);
        Dest donation = destMap.get("donation");
        assertNotNull(donation);
        DestSection creeperAction = donation.getSection("creeper");
        assertNotNull(creeperAction);
        assertEquals("creeper", creeperAction.getName());
        assertNotNull(creeperAction.getCommands());
        assertNotNull(creeperAction.getCondition());
        assertTrue(creeperAction.getCondition().isTrue(cmdContext.getObjectContext()));
        creeperAction.getCommands().get(0).execute(cmdContext);
        verify(executor).execute(notNull(), eq("summon creeper"));
        DestSection zombieAction = donation.getSection("zombie");
        assertNotNull(zombieAction);
        assertEquals("zombie", zombieAction.getName());
        assertNotNull(zombieAction.getCommands());
        assertNotNull(zombieAction.getCondition());
        assertTrue(zombieAction.getCondition().isTrue(cmdContext.getObjectContext()));
        zombieAction.getCommands().get(0).execute(cmdContext);
        verify(executor).execute(notNull(), eq("summon zombie"));
        DestSection skeletonAction = destMap.getSection(Path.of("donation", "skeleton"));
        assertNotNull(skeletonAction);
        assertFalse(skeletonAction.getCondition().isTrue(cmdContext.getObjectContext()));
        skeletonAction.getCommands().get(0).execute(cmdContext);
        verify(executor).execute(notNull(), eq("summon skeleton"));
    }

}