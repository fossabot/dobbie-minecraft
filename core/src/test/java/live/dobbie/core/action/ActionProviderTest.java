package live.dobbie.core.action;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.messaged.Messaged;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ActionProviderTest {

    @Test
    void basicTest() {
        ActionProvider provider = new ActionProvider();
        Action.Factory factory = Mockito.mock(Action.Factory.class);
        Trigger trigger = Mockito.mock(Trigger.class);
        provider.registerFactory(Trigger.class, factory);
        assertEquals(factory, provider.findFactory(trigger));
    }

    @Test
    void basicInheritTest0() {
        ActionProvider provider = new ActionProvider();
        Action action = Mockito.mock(Action.class);
        provider.registerFactory(Trigger.class, tr -> action);
        assertEquals(action, provider.get(Mockito.mock(TestTrigger.class)));
    }

    @Test
    void complexTest() {
        ActionProvider provider = new ActionProvider();
        Action action = Mockito.mock(Action.class);
        Action messagedAction = Mockito.mock(Action.class);
        Action serviceAction = Mockito.mock(Action.class);
        provider.registerFactory(Trigger.class, tr -> action);
        provider.registerFactory(Messaged.class, tr -> messagedAction);
        provider.registerFactory(ServiceTrigger.class, tr -> serviceAction);
        assertSame(action, provider.get(Mockito.mock(TestTrigger.class)));
        assertSame(messagedAction, provider.get(Mockito.mock(Messaged.class)));
        assertSame(serviceAction, provider.get(Mockito.mock(ServiceTrigger.class)));
    }

    public interface TestTrigger extends Trigger {
    }

    public interface ServiceTrigger extends Authored, Messaged {
        @Override
        default @NonNull LocString toLocString(@NonNull Loc loc) {
            return loc.args()
                    .copy(Authored.super.toLocString(loc))
                    .copy(Messaged.super.toLocString(loc));
        }
    }

}