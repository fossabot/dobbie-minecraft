package live.dobbie.core.service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IdSchedulerServiceTest {

    private ScheduledExecutorService scheduledExecutorService;
    private IdSchedulerService service;

    @BeforeEach
    void setUp() {
        scheduledExecutorService = mock(ScheduledExecutorService.class);
        when(scheduledExecutorService.scheduleWithFixedDelay(notNull(), anyLong(), anyLong(), notNull()))
                .thenReturn(mock(ScheduledFuture.class));
        service = new IdSchedulerService(scheduledExecutorService) {
            @Override
            Task createTask(Object identifier, Consumer<IdScheduledTask> r) {
                return spy(new Task(identifier, r));
            }
        };
    }

    @Test
    void scheduleAfterTest() {
        service.scheduleAfter(IdTask.name("test"), () -> {
        }, 5000L);
        verify(scheduledExecutorService).schedule((Runnable) notNull(), eq(5000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void scheduleAfterCancellingOldTest() {
        Runnable r = mock(Runnable.class);
        IdScheduledTask test0 = service.scheduleAfter(IdTask.name("test"), r, 5000L);
        IdScheduledTask test1 = service.scheduleAfter(IdTask.name("test"), r, 5000L);
        verify(test0).cancel();
        verify(test1, times(0)).cancel();
    }

    @Test
    void scheduleRepeatingTest() {
        IdSchedulerService.Task task = (IdSchedulerService.Task) service.scheduleRepeating(IdTask.name("test"), t -> {
        }, 5000L, 10000L);
        assertNotNull(task.future);
        verify(scheduledExecutorService).scheduleWithFixedDelay(notNull(), eq(5000L), eq(10000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void scheduleRepeatingCancellingOldTest() {
        IdSchedulerService.Task test0 = (IdSchedulerService.Task)
                service.scheduleRepeating(IdTask.name("test"), t -> {
                }, 5000L);
        IdSchedulerService.Task test1 = (IdSchedulerService.Task)
                service.scheduleRepeating(IdTask.name("test"), t -> {
                }, 10000L);
        verify(test0).cancel();
        verify(test0.future).cancel(eq(true));
        verify(test1, times(0)).cancel();
    }

}