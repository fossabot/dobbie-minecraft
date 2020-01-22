package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.dest.cmd.script.AssertionScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ConditionalScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ScriptCmdParser;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.script.js.converter.DefaultValueConverter;
import live.dobbie.core.script.js.converter.PrimitiveJSConverter;
import live.dobbie.core.script.js.converter.TypedValueConverter;
import live.dobbie.core.substitutor.VarProvider;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.environment.Environment;
import live.dobbie.core.substitutor.plain.PlainSubstitutorParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class SequentalCmdParserTest {

    private PlainCmd.Executor executor;
    private CmdParser parser;
    private CmdContext context;

    @Test
    void basicTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("hello, world!");
        assertNotNull(cmd);
        cmd.execute(context);
        verify(executor).execute(notNull(), eq("hello, world!"));
    }

    @Test
    void verifyTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!verify verification.shouldBeTrue");
        assertNotNull(cmd);
        cmd.execute(context);
    }

    @Test
    void verifyFailTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!verify verification.shouldBeFalse");
        assertNotNull(cmd);
        assertThrows(AssertionCmdFailedException.class, () -> cmd.execute(context));
    }

    @Test
    void assertTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!assert verification.shouldBeTrue");
        assertNotNull(cmd);
        cmd.execute(context);
    }

    @Test
    void assertFailTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!assert verification.shouldBeFalse");
        assertNotNull(cmd);
        assertThrows(AssertionCmdFailedException.class, () -> cmd.execute(context));
    }

    @Test
    void jsIfTrueTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!jsif (verification.shouldBeTrue) it is true!");
        assertNotNull(cmd);
        cmd.execute(context);
        verify(executor).execute(notNull(), eq("it is true!"));
    }

    @Test
    void jsIfFalseTest() throws ParserException, CmdExecutionException {
        Cmd cmd = parser.parse("#!jsif (verification.shouldBeFalse) it is true!");
        assertNotNull(cmd);
        cmd.execute(context);
        verify(executor, times(0)).execute(notNull(), eq("it is true!"));
    }

    @BeforeEach
    void setup() {
        PlainCmd.Executor cmdExecutor = Mockito.mock(PlainCmd.Executor.class);
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf, TypedValueConverter.builder()
                .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                .setFallbackConverter(DefaultValueConverter.INSTANCE)
                .build());
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        SequentalCmdParser cmdParser = new SequentalCmdParser();
        cmdParser.registerParser(
                new AssertionCmd.Parser(Collections.singletonList("verify")),
                new AssertionScriptCmdParser<>(Collections.singletonList("assert"), executor, contextFactory, compiler),
                new ConditionalScriptCmdParser<>(Collections.singletonList("jsif"), executor, contextFactory, compiler, cmdParser),
                new ScriptCmdParser<>(Collections.singletonList("js"), executor, contextFactory, compiler),
                new SubstitutorCmd.Parser(new PlainSubstitutorParser())
        );
        ObjectContext oCtx = SimpleContext.builder()
                .set(Path.of("foo"), Primitive.of("bar"))
                .set(Path.of("verification", "shouldBeTrue"), Primitive.of(true))
                .set(Path.of("verification", "shouldBeFalse"), Primitive.of(false))
                .build();
        VarProvider varProvider = Mockito.mock(VarProvider.class);
        when(varProvider.getVar(eq("foo"))).thenReturn("world");
        when(varProvider.requireVar(notNull())).thenCallRealMethod();
        Env env = new Environment(Collections.singletonMap(VarProvider.class, varProvider));
        CmdContext context = new CmdContext(oCtx, cmdExecutor, env);
        this.executor = cmdExecutor;
        this.parser = cmdParser;
        this.context = context;
    }

}