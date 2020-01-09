package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.script.js.JSScript;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.substitutor.environment.Env;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AssertionScriptCmdParserTest {

    @Test
    void assertTrueTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        AssertionScriptCmdParser<JSScript, JSScriptContext> parser = new AssertionScriptCmdParser<>(
                Collections.singletonList("assert"), executor, contextFactory, compiler);
        Cmd cmd = parser.parse("#!assert foo.bar");
        assertNotNull(cmd);
        Foo foo = new Foo(true);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set("foo", foo).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
    }

    @Test
    void assertFalseTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        AssertionScriptCmdParser<JSScript, JSScriptContext> parser = new AssertionScriptCmdParser<>(
                Collections.singletonList("assert"), executor, contextFactory, compiler);
        Cmd cmd = parser.parse("#!assert foo.bar");
        assertNotNull(cmd);
        Foo foo = new Foo(false);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set("foo", foo).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        Assertions.assertThrows(AssertionCmdFailedException.class, () -> cmd.execute(cmdContext));
    }

    @AllArgsConstructor
    public static class Foo {
        public boolean bar;
    }

}