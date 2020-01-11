package live.dobbie.core.substitutor.plain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VarConverterTest {

    @Test
    void escapingTest() {
        VarConverter vc = VarConverter.JsonEscaping.INSTANCE;
        assertEquals("hello, \\\"world\\\"", vc.convertVarValue("hello, \"world\""));
        assertEquals("hello, 'world'", vc.convertVarValue("hello, 'world'"));
    }

    @Test
    void doubleEscapingTest() {
        VarConverter vc = VarConverter.DoubleJsonEscaping.INSTANCE;
        assertEquals("hello, \\\\\\\"world\\\\\\\"", vc.convertVarValue("hello, \"world\""));
        assertEquals("hello, 'world'", vc.convertVarValue("hello, 'world'"));
    }

}