package live.dobbie.core;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.scheduler.ActionScheduler;
import live.dobbie.core.config.DobbieConfig;
import live.dobbie.core.service.ServiceRegistry;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.TriggerErrorHandler;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DobbieTest {

    @Test
    void basicTest() {
        User user = Mockito.mock(User.class);
        Source source = Mockito.mock(Source.class);
        Trigger trigger = Mockito.mock(Trigger.class);
        when(source.triggerList()).thenReturn(Collections.singletonList(trigger));
        when(source.triggerStream()).thenCallRealMethod();
        Action action = Mockito.mock(Action.class);
        Source.Factory.Provider sourceProvider = () -> Collections.singletonList(pl -> source);
        Action.Factory actionFactory = Mockito.mock(Action.Factory.class);
        when(actionFactory.createAction(trigger)).thenReturn(action);
        UserSettingsProvider userSettingsProvider = Mockito.mock(UserSettingsProvider.class);
        DobbieSettings settings = Mockito.mock(DobbieSettings.class);
        when(settings.getUserSettingsProvider()).thenReturn(userSettingsProvider);
        ISettings globalSettings = Mockito.mock(ISettings.class);
        when(settings.getGlobalSettings()).thenReturn(globalSettings);
        when(globalSettings.requireValue(DobbieConfig.Timer.Ticks.class)).thenReturn(new DobbieConfig.Timer.Ticks(1, 1));
        ActionScheduler scheduler = Mockito.mock(ActionScheduler.class);
        Dobbie dobbie = new Dobbie(settings, sourceProvider, actionFactory, scheduler, Mockito.mock(TriggerErrorHandler.class), Mockito.mock(ServiceRegistry.class));
        dobbie.registerUser(user);
        verify(scheduler).registerUser(user);
        verify(userSettingsProvider).registerUser(user);
        assertNotNull(dobbie.getInstance(user));
        dobbie.tick();
        dobbie.unregisterUser(user);
        verify(scheduler).unregisterUser(user);
        verify(userSettingsProvider).unregisterUser(user);
        assertNull(dobbie.getInstance(user));
        verify(source).cleanup();
        dobbie.cleanup();
        verify(scheduler).schedule(action);
        verify(scheduler).cleanup();
    }

    @Test
    void testAlreadyRegistered() {
        User user = Mockito.mock(User.class);
        Source.Factory.Provider sourceProvider = Mockito.mock(Source.Factory.Provider.class);
        Action.Factory actionFactory = Mockito.mock(Action.Factory.class);
        UserSettingsProvider userSettingsProvider = Mockito.mock(UserSettingsProvider.class);
        DobbieSettings settings = Mockito.mock(DobbieSettings.class);
        when(settings.getUserSettingsProvider()).thenReturn(userSettingsProvider);
        ISettings globalSettings = Mockito.mock(ISettings.class);
        when(settings.getGlobalSettings()).thenReturn(globalSettings);
        when(globalSettings.requireValue(DobbieConfig.Timer.Ticks.class)).thenReturn(new DobbieConfig.Timer.Ticks(1, 1));
        ActionScheduler scheduler = Mockito.mock(ActionScheduler.class);

        Dobbie dobbie = Mockito.spy(new Dobbie(settings, sourceProvider, actionFactory, scheduler, Mockito.mock(TriggerErrorHandler.class), Mockito.mock(ServiceRegistry.class)));

        Dobbie.Instance oldInstance = Mockito.mock(Dobbie.Instance.class);
        Dobbie.Instance newInstance = Mockito.mock(Dobbie.Instance.class);
        assertNotSame(oldInstance, newInstance);

        // register player for the first time
        doReturn(oldInstance).when(dobbie).createInstance(user);
        dobbie.registerUser(user);

        // register player again (without calling unregister!)
        doReturn(newInstance).when(dobbie).createInstance(user);
        dobbie.registerUser(user);

        // verify oldInstance is cleaned up and nothing more
        verify(oldInstance).cleanup();
        verify(newInstance, times(0)).cleanup();
        assertEquals(newInstance, dobbie.getInstance(user), "old instance was not replaced by new one");

        // finally unregistering player
        dobbie.unregisterUser(user);
        assertNull(dobbie.getInstance(user));
        verify(newInstance).cleanup();
    }

    @Test
    void refreshSettingsTest() {
        User user = Mockito.mock(User.class);
        UserSettingsProvider userSettingsProvider = Mockito.mock(UserSettingsProvider.class);
        DobbieSettings settings = Mockito.mock(DobbieSettings.class);
        when(settings.getUserSettingsProvider()).thenReturn(userSettingsProvider);
        ISettings globalSettings = Mockito.mock(ISettings.class);
        when(settings.getGlobalSettings()).thenReturn(globalSettings);
        when(globalSettings.requireValue(DobbieConfig.Timer.Ticks.class)).thenReturn(new DobbieConfig.Timer.Ticks(1, 1));

        Dobbie dobbie = Mockito.spy(new Dobbie(
                settings,
                Mockito.mock(Source.Factory.Provider.class),
                Mockito.mock(Action.Factory.class),
                Mockito.mock(ActionScheduler.class),
                Mockito.mock(TriggerErrorHandler.class),
                Mockito.mock(ServiceRegistry.class)
        ));

        dobbie.tick();
        verify(settings).refresh();
    }

}