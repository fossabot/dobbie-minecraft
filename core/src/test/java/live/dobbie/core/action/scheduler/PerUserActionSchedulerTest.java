package live.dobbie.core.action.scheduler;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionExecutionException;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.cancellable.Cancellable;
import live.dobbie.core.user.User;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PerUserActionSchedulerTest {

    private ActionScheduler fallbackScheduler;
    private ExecutorService executorService;
    private Loc loc = new Loc();


    private PerUserActionScheduler scheduler;

    @BeforeEach
    void setUp() {
        fallbackScheduler = Mockito.mock(ActionScheduler.class);
        doAnswer(invocation -> {
            Action action = invocation.getArgument(0);
            action.execute();
            return null;
        }).when(fallbackScheduler).schedule(notNull());
        executorService = Mockito.mock(ExecutorService.class);
        when(executorService.submit((Runnable) notNull())).then((Answer<Object>) invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        });
        scheduler = new PerUserActionScheduler(fallbackScheduler, loc, user -> executorService);
    }

    @Test
    void basicTest() throws ActionExecutionException {
        User user = Mockito.mock(User.class);
        scheduler.registerUser(user);
        UserRelatedTrigger trigger = Mockito.mock(UserRelatedTrigger.class);
        when(trigger.getUser()).thenReturn(user);
        Action action = Mockito.spy(new Action.OfRunnable(trigger, loc.withKey("test action"), () -> {
        }));
        scheduler.schedule(action);
        verify(fallbackScheduler, times(0)).schedule(any());
        verify(executorService).submit((Runnable) notNull());
        verify(action).execute();
    }

    @Test
    void noRegistrationFailTest() {
        User user = Mockito.mock(User.class);
        UserRelatedTrigger trigger = Mockito.mock(UserRelatedTrigger.class);
        when(trigger.getUser()).thenReturn(user);
        Action action = Mockito.spy(new Action.OfRunnable(trigger, loc.withKey("test action"), () -> {
        }));
        assertThrows(RuntimeException.class, () -> scheduler.schedule(action));
    }

    @Test
    void noUserTest() throws ActionExecutionException {
        User user = Mockito.mock(User.class);
        scheduler.registerUser(user);
        Trigger trigger = Mockito.mock(Trigger.class);
        Action action = Mockito.spy(new Action.OfRunnable(trigger, loc.withKey("test action"), () -> {
        }));
        scheduler.schedule(action);
        verify(fallbackScheduler).schedule(any());
        verify(executorService, times(0)).submit((Runnable) notNull());
        verify(action).execute();
    }

    @Test
    void cleanupTest() {
        User user = Mockito.mock(User.class);
        scheduler.registerUser(user);
        scheduler.registerUser(Mockito.mock(User.class)); // 2 users
        scheduler.cleanup();
        verify(executorService, times(2)).shutdown();
        verify(fallbackScheduler).cleanup();
    }

    @Test
    void errorExecutingActionTest() {
        User user = Mockito.mock(User.class);
        scheduler.registerUser(user);
        UserRelatedCancellableTrigger trigger = Mockito.mock(UserRelatedCancellableTrigger.class);
        when(trigger.getUser()).thenReturn(user);
        Action action = Mockito.spy(new Action.OfRunnable(trigger, loc.withKey("test action"), () -> {
            throw new RuntimeException();
        }));
        scheduler.schedule(action);
        verify(trigger).cancel(notNull());
    }


    interface UserRelatedCancellableTrigger extends UserRelatedTrigger, Cancellable {
        @Override
        default @NonNull LocString toLocString(@NonNull Loc loc) {
            return loc.args()
                    .copy(UserRelatedTrigger.super.toLocString(loc))
                    .copy(Cancellable.super.toLocString(loc));
        }
    }

}