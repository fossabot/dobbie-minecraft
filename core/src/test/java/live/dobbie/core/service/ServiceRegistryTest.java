package live.dobbie.core.service;

import live.dobbie.core.user.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRegistryTest {
    @Test
    void basicTest() {
        ServiceRegistry services = ServiceRegistry.builder().registerFactory(TestService.class, new TestServiceFactory()).build();
        User user = Mockito.mock(User.class);
        services.registerUser(user);
        ServiceRef<TestService> testServiceRef = services.createReference(TestService.class, user);
        assertNotNull(testServiceRef);
        TestService testService = testServiceRef.getService();
        assertSame(testService, testServiceRef.getService());
        verify(testServiceRef, times(0)).cleanup();
        services.unregisterUser(user);
        verify(testServiceRef).cleanup();
    }

    @Test
    void multiUserTest() {
        ServiceRegistry services = ServiceRegistry.builder().registerFactory(TestService.class, new TestServiceFactory()).build();
        User user0 = Mockito.mock(User.class), user1 = Mockito.mock(User.class);
        services.registerUser(user0);
        services.registerUser(user1);
        ServiceRef<TestService>
                testServiceRef0 = services.createReference(TestService.class, user0),
                testServiceRef1 = services.createReference(TestService.class, user1);
        assertNotSame(testServiceRef0, testServiceRef1);
    }

    @RequiredArgsConstructor
    public static class TestService implements Service {
        @NonNull
        @Getter
        User user;

        @Override
        public void cleanup() {
        }
    }

    public static class TestServiceFactory implements ServiceRef.Factory<TestService> {
        @Override
        public @NonNull ServiceRef<TestService> createServiceRef(@NonNull ServiceRefProvider provider, @NonNull User user) {
            ServiceRef<TestService> ref = Mockito.mock(ServiceRef.class);
            TestService s = Mockito.spy(new TestService(user));
            when(ref.getService()).thenReturn(s);
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    s.cleanup();
                    return null;
                }
            }).when(ref).cleanup();
            return ref;
        }
    }
}