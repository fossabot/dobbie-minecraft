package live.dobbie.core.action;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.trigger.Trigger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

class ActionTest {

    @Test
    void listThrowsTest() {
        Loc loc = new Loc();
        Trigger trigger = Mockito.mock(Trigger.class);
        List<Action> l1 = Collections.singletonList(new Action.WithDescription(trigger, loc.withKey("test action")) {
            @Override
            public void execute() {
                throw new IllegalArgumentException();
            }
        });
        Action.List list = new Action.List(trigger, loc.withKey("test list"), l1);
        Assertions.assertThrows(ActionExecutionException.class, list::execute);
    }

}