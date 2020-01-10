package live.dobbie.core.service;

import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.ISettingsListener;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsBasedServiceRefTest {

    @Test
    void basicTest() {
        User user = Mockito.mock(User.class);
        ISettings settings = Mockito.mock(ISettings.class);
        ISettingsListener[] listener = new ISettingsListener[1];
        when(settings.registerListener(eq(Val.class), notNull())).then((Answer<SettingsSubscription<Val>>) invocation -> {
            listener[0] = invocation.getArgument(1);
            return mock(SettingsSubscription.class);
        });
        SettingsBasedServiceRef<TestService, Val> ref = new SettingsBasedServiceRef<>(
                Val.class, "test", user, settings,
                (user1, value) -> new TestService(user1, Validate.notNull(value)),
                mock(ServiceRefProvider.class)
        );
        assertNotNull(listener[0]);
        assertFalse(ref.isAvailable());
        assertThrows(ServiceUnavailableException.class, () -> ref.getService());
        Val valA = new Val("a");
        listener[0].onSettingsChanged(valA);
        assertTrue(ref.isAvailable());
        assertNotNull(ref.getService());
    }

    @Test
    void updateValueTest() {
        User user = Mockito.mock(User.class);
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(Val.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        SettingsBasedServiceRef<TestService, Val> ref = new SettingsBasedServiceRef<>(
                Val.class, "test", user, settings,
                (user1, value) -> new TestService(user1, Validate.notNull(value)),
                mock(ServiceRefProvider.class)
        );
        Val valA = new Val("a");
        ref.onSettingsChanged(valA);
        TestService serviceA = ref.getService();
        assertNotNull(serviceA);
        Val valB = new Val("b");
        ref.onSettingsChanged(valB);
        TestService serviceB = ref.getService();
        assertNotNull(serviceB);
        assertNotSame(serviceA, serviceB);
    }

    @Test
    void cleanupTest() {
        User user = Mockito.mock(User.class);
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(Val.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        SettingsBasedServiceRef<TestService, Val> ref = new SettingsBasedServiceRef<>(
                Val.class, "test", user, settings,
                (user1, value) -> Mockito.spy(new TestService(user1, Validate.notNull(value))),
                mock(ServiceRefProvider.class)
        );
        Val val = new Val("a");
        ref.onSettingsChanged(val);
        TestService service = ref.getService();
        assertEquals("a", service.getVal().getStr());
        ref.cleanup();
        verify(service, times(1)).cleanup();
        assertFalse(ref.isAvailable());
        Assertions.assertThrows(ServiceUnavailableException.class, () -> ref.getService());
        Assertions.assertThrows(ServiceUnavailableException.class, () -> ref.registerListener(s -> {
        }));
    }

    @Test
    void listenerTest() {
        User user = Mockito.mock(User.class);
        ISettings settings = Mockito.mock(ISettings.class);
        when(settings.registerListener(eq(Val.class), notNull())).thenReturn(Mockito.mock(SettingsSubscription.class));
        SettingsBasedServiceRef<TestService, Val> ref = new SettingsBasedServiceRef<>(
                Val.class, "test", user, settings,
                (user1, value) -> Mockito.spy(new TestService(user1, Validate.notNull(value))),
                mock(ServiceRefProvider.class)
        );
        Val val = new Val("a");
        ref.onSettingsChanged(val);
        ServiceRefListener<TestService> listener = Mockito.mock(ServiceRefListener.class);
        ref.registerListener(listener, true);
        TestService service = ref.getService();
        verify(listener).onReferenceUpdated(eq(service), eq(null));
    }

    @Value
    public static class TestService implements Service {
        @NonNull User user;
        @NonNull Val val;

        @Override
        public void cleanup() {
        }
    }

    @Value
    public static class Val implements ISettingsValue {
        @NonNull String str;
    }

}