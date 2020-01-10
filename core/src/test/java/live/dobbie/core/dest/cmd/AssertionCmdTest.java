package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.substitutor.environment.Env;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AssertionCmdTest {

    @Test
    void assertTrueTest() throws ParserException, CmdExecutionException {
        AssertionCmd.Parser parser = new AssertionCmd.Parser(Collections.singletonList("assert"));
        Cmd cmd = parser.parse("#!assert foo.bar");
        assertNotNull(cmd);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set(Path.of("foo", "bar"), Primitive.of(true)).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
    }

    @Test
    void assertFalseTest() throws ParserException, CmdExecutionException {
        AssertionCmd.Parser parser = new AssertionCmd.Parser(Collections.singletonList("assert"));
        Cmd cmd = parser.parse("#!assert foo.bar");
        assertNotNull(cmd);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set(Path.of("foo", "bar"), Primitive.of(false)).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        Assertions.assertThrows(AssertionCmdFailedException.class, () -> cmd.execute(cmdContext));
    }

    @Test
    void negateTest() throws ParserException, CmdExecutionException {
        AssertionCmd.Parser parser = new AssertionCmd.Parser(Collections.singletonList("assert"));
        Cmd cmd = parser.parse("#!assert !foo.bar");
        assertNotNull(cmd);
        CmdContext cmdContext = new CmdContext(SimpleContext.builder().set(Path.of("foo", "bar"), Primitive.of(false)).build(), Mockito.mock(PlainCmd.Executor.class), Mockito.mock(Env.class));
        cmd.execute(cmdContext);
    }

    @Test
    void badNegateTest() throws ParserException, CmdExecutionException {
        AssertionCmd.Parser parser = new AssertionCmd.Parser(Collections.singletonList("assert"));
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!assert !"));
    }

}