package live.dobbie.core.settings.source.jackson;

import live.dobbie.core.path.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    @Test
    void isEmpty() {
        assertTrue(Path.EMPTY.isEmpty());
        assertTrue(Path.of().isEmpty());
        //assertTrue(new Path(Collections.emptyList()).isEmpty());
        assertFalse(Path.of("foo").isEmpty());
    }

    @Test
    void length() {
        assertEquals(0, Path.EMPTY.length());
        assertEquals(0, Path.of().length());
        assertEquals(1, Path.of("foo").length());
    }

    @Test
    void parent() {
        assertNull(Path.EMPTY.parent());
        assertNull(Path.of().parent());
        assertEquals(Path.EMPTY, Path.of("foo").parent());
        assertEquals(Path.of("foo"), Path.of("foo", "bar").parent());
    }

    @Test
    void merge() {
        assertEquals(Path.EMPTY, Path.EMPTY.merge(Path.EMPTY));
        assertEquals(Path.of("foo"), Path.EMPTY.merge("foo"));
        assertEquals(Path.of("foo", "bar"), Path.of("foo").merge("bar"));
        assertEquals(Path.of("foo"), Path.of("foo").merge(Path.EMPTY));
    }

    @Test
    void subset() {
        assertEquals(Path.EMPTY, Path.EMPTY.subset(0, 0));
        assertEquals(Path.of("foo"), Path.of("foo", "bar").subset(0, 1));
        assertEquals(Path.of("bar", "tar"), Path.of("foo", "bar", "tar").subset(1, 2));
        assertEquals(Path.of("bar"), Path.of("foo", "bar", "tar").subset(1, 2).subset(0, 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Path.of("foo").subset(1, 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Path.of("foo", "bar").subset(0, 3));
    }

    @Test
    void testToString() {
        assertEquals("", Path.toString(Path.EMPTY));
        assertEquals("foo", Path.toString(Path.of("foo")));
        assertEquals("foo.bar", Path.toString(Path.of("foo", "bar")));
    }

    @Test
    void testToString1() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Path.toString(Path.EMPTY, 1));
        assertEquals("", Path.toString(Path.of("foo"), 1));
        assertEquals("foo", Path.toString(Path.of("foo", "bar"), 1));
        assertEquals("foo", Path.toString(Path.of("foo", "bar", "foo"), 2));
        assertEquals("foo.bar", Path.toString(Path.of("foo", "bar", "foo"), 1));
    }

    @Test
    void testToString2() {
        assertEquals("", Path.toString(Path.EMPTY, " -> ", 0));
        assertEquals("foo -> bar", Path.toString(Path.of("foo", "bar"), " -> ", 0));
        assertEquals("foo -> bar -> foo", Path.toString(Path.of("foo", "bar", "foo"), " -> ", 0));
        assertEquals("foo -> bar", Path.toString(Path.of("foo", "bar", "foo"), " -> ", 1));
    }

    @Test
    void iteratorTest() {
        Iterator<String> i;

        i = Path.of("bar").iterator();
        assertTrue(i.hasNext());
        assertEquals("bar", i.next());
        assertFalse(i.hasNext());

        i = Path.of("foo", "bar", "tar").subset(1, 1).iterator();
        assertTrue(i.hasNext());
        assertEquals("bar", i.next());
        assertFalse(i.hasNext());
    }
}