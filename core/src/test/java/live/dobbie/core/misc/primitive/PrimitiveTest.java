package live.dobbie.core.misc.primitive;

import live.dobbie.core.exception.ParserRuntimeException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static live.dobbie.core.misc.primitive.Primitive.of;
import static live.dobbie.core.misc.primitive.Primitive.parse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrimitiveTest {

    @Test
    void ofObjectTest() {
        assertEquals(of("hello"), of((Object) "hello"));
        assertEquals(of(5), of((Object) 5));
        assertEquals(of(true), of((Object) true));

        Instant now = Instant.now();
        assertEquals(of(now), of((Object) now));

        assertThrows(ParserRuntimeException.class, () -> of(new Object()));
    }

    @Test
    void parseTest() {
        assertEquals(of(155), parse("155"));
        assertEquals(of(0.5f), parse("0.5"));
        assertEquals(of(0.123456789d), parse("0.123456789"));
        assertEquals(of(12345678901L), parse("12345678901"));
    }

}