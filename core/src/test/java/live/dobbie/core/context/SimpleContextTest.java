package live.dobbie.core.context;

import live.dobbie.core.context.primitive.Primitive;
import live.dobbie.core.path.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleContextTest {

    @Test
    void badPathTest() {
        assertThrows(IndexOutOfBoundsException.class, () -> SimpleContext.builder().set(Path.EMPTY, Primitive.of(true)));
    }

    @Test
    void testMixedVarFirst() {
        SimpleContext.Builder set = SimpleContext.builder().set(Path.of("foo", "bar"), Primitive.of(true));
        assertThrows(IllegalArgumentException.class, () -> set.set("foo", new Foo()));
    }

    @Test
    void testMixedObjFirst() {
        SimpleContext.Builder set = SimpleContext.builder().set("foo", new Foo());
        assertThrows(IllegalArgumentException.class, () -> set.set(Path.of("foo", "bar"), Primitive.of(true)));
    }

    private static class Foo {
    }

}