package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.dest.cmd.Cmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.dest.cmd.PlainCmd;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.script.js.JSScript;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.substitutor.environment.Env;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.ContextFactory;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class ScriptCmdParserTest {

    @Test
    void basicTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        ScriptCmdParser<JSScript, JSScriptContext> parser = new ScriptCmdParser<>(Collections.singletonList("js"), executor, contextFactory, compiler);
        Cmd cmd = parser.parse("#!js foo.bar()");
        assertNotNull(cmd);
        Foo foo = Mockito.mock(Foo.class);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set("foo", foo).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
        verify(foo).bar();
    }

    public static class Foo {
        public void bar() {
        }
    }

}