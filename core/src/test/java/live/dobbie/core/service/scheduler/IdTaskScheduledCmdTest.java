package live.dobbie.core.service.scheduler;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IdTaskScheduledCmdTest {

    @Test
    void executeAfterTest() throws CmdExecutionException {
        User user = mock(User.class);
        IdTaskScheduler scheduler = mock(IdTaskScheduler.class);
        ServiceRef<IdTaskScheduler> serviceRef = mock(ServiceRef.class);
        when(serviceRef.getService()).thenReturn(scheduler);
        ServiceRefProvider serviceRefProvider = mock(ServiceRefProvider.class);
        when(serviceRefProvider.createReference(eq(IdTaskScheduler.class), eq(user))).thenReturn(serviceRef);
        Cmd enclosedCmd = mock(Cmd.class);
        long waitTime = 1L;
        IdTaskScheduledCmd.ExecuteAfter after = new IdTaskScheduledCmd.ExecuteAfter(serviceRefProvider, enclosedCmd, waitTime);
        CmdContext context = new CmdContext(user, mock(Trigger.class), mock(ObjectContext.class), mock(PlainCmd.Executor.class), mock(Env.class));
        after.execute(context);
        verify(scheduler).scheduleAfter(notNull(), eq(waitTime));
    }

    @Test
    void repeatEveryTest() throws CmdExecutionException {
        User user = mock(User.class);
        IdTaskScheduler scheduler = mock(IdTaskScheduler.class);
        ServiceRef<IdTaskScheduler> serviceRef = mock(ServiceRef.class);
        when(serviceRef.getService()).thenReturn(scheduler);
        ServiceRefProvider serviceRefProvider = mock(ServiceRefProvider.class);
        when(serviceRefProvider.createReference(eq(IdTaskScheduler.class), eq(user))).thenReturn(serviceRef);
        Cmd enclosedCmd = mock(Cmd.class);
        IdTaskScheduledCmd.RepeatEvery repeatEvery = new IdTaskScheduledCmd.RepeatEvery(serviceRefProvider, enclosedCmd, "test", 1L, 2L);
        CmdContext context = new CmdContext(user, mock(Trigger.class), mock(ObjectContext.class), mock(PlainCmd.Executor.class), mock(Env.class));
        repeatEvery.execute(context);
        verify(scheduler).scheduleRepeating(eq(IdTask.name("test")), notNull(), eq(1L), eq(2L));
    }

    @Test
    void parseExecuteAfterTest() throws CmdExecutionException, ParserException {
        User user = mock(User.class);
        IdTaskScheduler scheduler = mock(IdTaskScheduler.class);
        ServiceRef<IdTaskScheduler> serviceRef = mock(ServiceRef.class);
        when(serviceRef.getService()).thenReturn(scheduler);
        ServiceRefProvider serviceRefProvider = mock(ServiceRefProvider.class);
        when(serviceRefProvider.createReference(eq(IdTaskScheduler.class), eq(user))).thenReturn(serviceRef);
        Cmd enclosedCmd = mock(Cmd.class);
        CmdParser enclosedCmdParser = mock(CmdParser.class);
        when(enclosedCmdParser.parse(anyString())).thenReturn(enclosedCmd);
        IdTaskScheduledCmd.ExecuteAfter.Parser parser = new IdTaskScheduledCmd.ExecuteAfter.Parser(serviceRefProvider, enclosedCmdParser);
        IdTaskScheduledCmd.ExecuteAfter cmd = (IdTaskScheduledCmd.ExecuteAfter) parser.parse("3000 hello, world!");
        assertNotNull(cmd);
        assertEquals(3000L, cmd.waitTime);
        verify(enclosedCmdParser).parse((String) argThat(s -> s.equals("hello, world!")));
    }

    @Test
    void parseRepeatEveryTest() throws CmdExecutionException, ParserException {
        User user = mock(User.class);
        IdTaskScheduler scheduler = mock(IdTaskScheduler.class);
        ServiceRef<IdTaskScheduler> serviceRef = mock(ServiceRef.class);
        when(serviceRef.getService()).thenReturn(scheduler);
        ServiceRefProvider serviceRefProvider = mock(ServiceRefProvider.class);
        when(serviceRefProvider.createReference(eq(IdTaskScheduler.class), eq(user))).thenReturn(serviceRef);
        Cmd enclosedCmd = mock(Cmd.class);
        CmdParser enclosedCmdParser = mock(CmdParser.class);
        when(enclosedCmdParser.parse(anyString())).thenReturn(enclosedCmd);
        IdTaskScheduledCmd.RepeatEvery.Parser parser = new IdTaskScheduledCmd.RepeatEvery.Parser(serviceRefProvider, enclosedCmdParser);
        IdTaskScheduledCmd.RepeatEvery cmd = (IdTaskScheduledCmd.RepeatEvery) parser.parse("test 3000 5000 hello, world!");
        assertNotNull(cmd);
        assertEquals(IdTask.name("test"), cmd.id);
        assertEquals(3000L, cmd.initialWait);
        assertEquals(5000L, cmd.waitBetween);
        verify(enclosedCmdParser).parse((String) argThat(s -> s.equals("hello, world!")));
    }

}