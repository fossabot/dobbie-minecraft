package live.dobbie.core.dest.cmd.script;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class ConditionalScriptCmdParserTest {

    @Test
    void trueTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        CmdParser mockCmdParser = Mockito.mock(CmdParser.class);
        Cmd mockCmd = Mockito.mock(Cmd.class);
        when(mockCmdParser.parse((Text) notNull())).thenReturn(mockCmd);
        ConditionalScriptCmdParser<JSScript, JSScriptContext> parser = new ConditionalScriptCmdParser<>(
                Collections.singletonList("jsif"),
                executor, contextFactory, compiler, mockCmdParser
        );
        Cmd cmd = parser.parse("#!jsif (foo.bar) hello, world!");
        verify(mockCmdParser).parse((Text) notNull());
        assertNotNull(cmd);
        Foo foo = new Foo(true);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set("foo", foo).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
        verify(mockCmd).execute(notNull());
    }

    @Test
    void falseTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        CmdParser mockCmdParser = Mockito.mock(CmdParser.class);
        Cmd mockCmd = Mockito.mock(Cmd.class);
        when(mockCmdParser.parse((Text) notNull())).thenReturn(mockCmd);
        ConditionalScriptCmdParser<JSScript, JSScriptContext> parser = new ConditionalScriptCmdParser<>(
                Collections.singletonList("jsif"),
                executor, contextFactory, compiler, mockCmdParser
        );
        Cmd cmd = parser.parse("#!jsif (foo.bar) hello, world!");
        assertNotNull(cmd);
        Foo foo = new Foo(false);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set("foo", foo).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
        verify(mockCmd, times(0)).execute(any());
    }

    @Test
    void emptyCmdFailTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        CmdParser mockCmdParser = Mockito.mock(CmdParser.class);
        Cmd mockCmd = Mockito.mock(Cmd.class);
        when(mockCmdParser.parse((Text) notNull())).thenReturn(mockCmd);
        ConditionalScriptCmdParser<JSScript, JSScriptContext> parser = new ConditionalScriptCmdParser<>(
                Collections.singletonList("jsif"),
                executor, contextFactory, compiler, mockCmdParser
        );
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif (foo.bar)"));
    }

    @Test
    void badParenthesisFailTest() throws ParserException, CmdExecutionException {
        ContextFactory cf = new ContextFactory();
        JSScriptExecutor executor = new JSScriptExecutor();
        JSScriptContext.Factory contextFactory = new JSScriptContext.Factory(cf);
        JSScriptCompiler compiler = new JSScriptCompiler(cf);
        CmdParser mockCmdParser = Mockito.mock(CmdParser.class);
        Cmd mockCmd = Mockito.mock(Cmd.class);
        when(mockCmdParser.parse((Text) notNull())).thenReturn(mockCmd);
        ConditionalScriptCmdParser<JSScript, JSScriptContext> parser = new ConditionalScriptCmdParser<>(
                Collections.singletonList("jsif"),
                executor, contextFactory, compiler, mockCmdParser
        );
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif ((foo.bar) hello, world!"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif (foo.bar)) hello, world!"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif hello, world!"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif foo.bar hello, world!"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif ()"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!jsif ((()) hello, world)"));
    }

    @AllArgsConstructor
    public static class Foo {
        public boolean bar;
    }
}