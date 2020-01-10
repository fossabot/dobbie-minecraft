package live.dobbie.core.util.io.mod;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ModSignalTest {

    @Test
    void unknownModSignalTest() {
        UnknownModSignal instance0 = new UnknownModSignal();
        UnknownModSignal instance1 = new UnknownModSignal();
        assertNotEquals(instance0, instance1);
    }

    @Test
    void longModSignal() {
        Object parent = new Object();
        LongModSignal signal0, signal1;

        signal0 = new LongModSignal(parent, 0);
        signal1 = new LongModSignal(parent, 0);
        assertEquals(signal0, signal1);

        signal1 = new LongModSignal(parent, 1);
        assertNotEquals(signal0, signal1);

        signal0 = new LongModSignal(new Object(), 1);
        assertNotEquals(signal0, signal1);
    }

    @Test
    void constModSignal() {
        Object object0 = new Object();
        ObjectModSignal instance0 = new ObjectModSignal(object0);
        ObjectModSignal instance1 = new ObjectModSignal(object0);
        assertEquals(instance0, instance1);

        Object object1 = new Object();
        instance1 = new ObjectModSignal(object1);
        assertNotEquals(instance0, instance1);
    }

}