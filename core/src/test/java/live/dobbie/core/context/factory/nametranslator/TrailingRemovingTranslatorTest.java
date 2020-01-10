package live.dobbie.core.context.factory.nametranslator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrailingRemovingTranslatorTest {

    @Test
    void test() {
        TrailingRemovingTranslator translator = new TrailingRemovingTranslator(
                VarNameTranslator.NONE,
                Arrays.asList("hello", "!"),
                Arrays.asList(")", "world"),
                Collections.emptyList());
        assertEquals("world!)", translator.translateClass("helloworld!)"));
        assertEquals("helloworld!", translator.translateMethod("helloworld!)"));
        assertEquals("oops", translator.translateField("oops"));
    }

}