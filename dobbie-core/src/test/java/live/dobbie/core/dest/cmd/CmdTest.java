package live.dobbie.core.dest.cmd;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static live.dobbie.core.dest.cmd.CmdResult.SHOULD_CONTINUE;
import static live.dobbie.core.dest.cmd.CmdResult.SHOULD_STOP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CmdTest {

    @Test
    void executeFromContinueTest() throws CmdExecutionException {
        CmdContext context = mock(CmdContext.class);
        Cmd cmd0 = cmdMock(SHOULD_CONTINUE);
        assertEquals(SHOULD_CONTINUE, Cmd.executeFrom(Collections.singletonList(cmd0), context));
        verify(cmd0).execute(context);
    }

    @Test
    void executeFromStopTest() throws CmdExecutionException {
        CmdContext context = mock(CmdContext.class);
        Cmd cmd0 = cmdMock(SHOULD_STOP);
        assertEquals(SHOULD_STOP, Cmd.executeFrom(Collections.singletonList(cmd0), context));
        verify(cmd0).execute(context);
    }

    @Test
    void executeFromMixedTest() throws CmdExecutionException {
        CmdContext context = mock(CmdContext.class);
        Cmd cmd0 = cmdMock(SHOULD_CONTINUE), cmd1 = cmdMock(SHOULD_STOP);
        assertEquals(SHOULD_STOP, Cmd.executeFrom(Arrays.asList(cmd0, cmd1), context));
        verify(cmd0).execute(context);
        verify(cmd1).execute(context);
    }

    @Test
    void executeFromInterruptTest() throws CmdExecutionException {
        CmdContext context = mock(CmdContext.class);
        Cmd cmd0 = cmdMock(SHOULD_STOP), cmd1 = cmdMock(SHOULD_CONTINUE);
        assertEquals(SHOULD_STOP, Cmd.executeFrom(Arrays.asList(cmd0, cmd1), context));
        verify(cmd0).execute(context);
        verify(cmd1, times(0)).execute(context);
    }

    static Cmd cmdMock(CmdResult result) throws CmdExecutionException {
        Cmd cmd = mock(Cmd.class);
        when(cmd.execute(notNull())).thenReturn(result);
        return cmd;
    }

}