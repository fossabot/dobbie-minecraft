package live.dobbie.core.action.factory;

import live.dobbie.core.action.Action;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.trigger.Ignorable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FallbackActionFactoryTest {

    @Test
    void ignorableTest() {
        Ignorable ignorable = mock(Ignorable.class);
        FallbackActionFactory factory = new FallbackActionFactory.Instance(new Loc());
        Action action = factory.createAction(ignorable);
        assertTrue(action instanceof Action.Empty);
    }

}