package live.dobbie.core.loc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocTest {

    @Test
    void basicTest() {
        Loc loc = new Loc();
        assertEquals("hello, world!", loc.withKey("hello, world!").build());
    }

    @Test
    void numericTest() {
        final String key = "hello, {number|world,worlds}";
        Loc loc = new Loc();
        assertEquals("hello, world", loc.withKey(key).set("number", 1).build());
        assertEquals("hello, worlds", loc.withKey(key).set("number", 2).build());
        assertEquals("hello, worlds", loc.withKey(key).set("number", 0).build());
    }

    @Test
    void lastNumericTest() {
        final String key = "hello, {number} {%|world,worlds}";
        Loc loc = new Loc();
        assertEquals("hello, 1 world", loc.withKey(key).set("number", 1).build());
        assertEquals("hello, 2 worlds", loc.withKey(key).set("number", 2).build());
        assertEquals("hello, 0 worlds", loc.withKey(key).set("number", 0).build());
    }

    @Test
    void remainingCharCopyTest() {
        final String key = "hello, {number}!";
        Loc loc = new Loc();
        assertEquals("hello, 1!", loc.withKey(key).set("number", 1).build());
    }

}