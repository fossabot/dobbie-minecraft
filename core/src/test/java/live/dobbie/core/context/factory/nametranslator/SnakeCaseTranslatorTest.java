package live.dobbie.core.context.factory.nametranslator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SnakeCaseTranslatorTest {

    @Test
    void test() {
        SnakeCaseTranslator translator = new SnakeCaseTranslator(VarNameTranslator.NONE);
        assertEquals("hello_world", translator.translateClass("HelloWorld"));
        assertEquals("hello_world", translator.translateMethod("HelloWorld"));
    }

}