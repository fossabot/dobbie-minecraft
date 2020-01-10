package live.dobbie.core.dest.cmd;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.dest.DestSection;
import live.dobbie.core.dest.DestSectionLocator;
import live.dobbie.core.path.Path;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GotoDestSectionCmdTest {

    @Test
    void basicTest() throws CmdExecutionException {
        User user = mock(User.class);
        Path path = Path.of("hello", "world");
        Cmd targetCmd = mock(Cmd.class);
        when(targetCmd.execute(notNull())).thenReturn(CmdResult.SHOULD_CONTINUE);
        DestSection destSection = mock(DestSection.class);
        when(destSection.getCommands()).thenReturn(Collections.singletonList(targetCmd));
        DestSectionLocator destSectionLocator = mock(DestSectionLocator.class);
        when(destSectionLocator.requireSection(eq(path))).thenReturn(destSection);
        DestSectionLocator.Factory locatorFactory = mock(DestSectionLocator.Factory.class);
        when(locatorFactory.create(user)).thenReturn(destSectionLocator);
        DestSectionLocator fallbackLocator = mock(DestSectionLocator.class);
        GotoDestSectionCmd gotoCmd = new GotoDestSectionCmd(path, locatorFactory, fallbackLocator, null);
        CmdContext context = new CmdContext(user, null, mock(ObjectContext.class), mock(PlainCmd.Executor.class), mock(Env.class));
        CmdResult result = gotoCmd.execute(context);
        assertEquals(CmdResult.SHOULD_CONTINUE, result);
    }

    @Test
    void noUserTest() throws CmdExecutionException {
        Path path = Path.of("hello", "world");
        Cmd targetCmd = mock(Cmd.class);
        when(targetCmd.execute(notNull())).thenReturn(CmdResult.SHOULD_CONTINUE);
        DestSection destSection = mock(DestSection.class);
        when(destSection.getCommands()).thenReturn(Collections.singletonList(targetCmd));
        DestSectionLocator destSectionLocator = mock(DestSectionLocator.class);
        when(destSectionLocator.requireSection(eq(path))).thenReturn(destSection);
        DestSectionLocator.Factory locatorFactory = mock(DestSectionLocator.Factory.class);
        DestSectionLocator fallbackLocator = mock(DestSectionLocator.class);
        when(fallbackLocator.requireSection(eq(path))).thenReturn(destSection);
        GotoDestSectionCmd gotoCmd = new GotoDestSectionCmd(path, locatorFactory, fallbackLocator, null);
        CmdContext context = new CmdContext(mock(ObjectContext.class), mock(PlainCmd.Executor.class), mock(Env.class));
        CmdResult result = gotoCmd.execute(context);
        assertEquals(CmdResult.SHOULD_CONTINUE, result);
    }

    @Test
    void forcedResultTest() throws CmdExecutionException {
        User user = mock(User.class);
        Path path = Path.of("hello", "world");
        Cmd targetCmd = mock(Cmd.class);
        when(targetCmd.execute(notNull())).thenReturn(CmdResult.SHOULD_CONTINUE);
        DestSection destSection = mock(DestSection.class);
        when(destSection.getCommands()).thenReturn(Collections.singletonList(targetCmd));
        DestSectionLocator destSectionLocator = mock(DestSectionLocator.class);
        when(destSectionLocator.requireSection(eq(path))).thenReturn(destSection);
        DestSectionLocator.Factory locatorFactory = mock(DestSectionLocator.Factory.class);
        when(locatorFactory.create(user)).thenReturn(destSectionLocator);
        DestSectionLocator fallbackLocator = mock(DestSectionLocator.class);
        GotoDestSectionCmd gotoCmd = new GotoDestSectionCmd(path, locatorFactory, fallbackLocator, CmdResult.SHOULD_STOP);
        CmdContext context = new CmdContext(user, null, mock(ObjectContext.class), mock(PlainCmd.Executor.class), mock(Env.class));
        CmdResult result = gotoCmd.execute(context);
        assertEquals(CmdResult.SHOULD_STOP, result);
    }


}