package live.dobbie.core.script.js.converter;

import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypedJSValueConverterTest {

    @Test
    void canConvertTest() {
        TypedJSValueConverter<L1> converter = new TypedJSValueConverter<L1>(L1.class) {

            @Override
            public L1 typedFromJs(Object object) {
                return null;
            }

            @Override
            public Object typedToJs(L1 object, @NonNull Scriptable scope, @NonNull Context context) {
                return null;
            }
        };
        assertTrue(converter.canConvert(L1.class));
        assertTrue(converter.canConvert(L2.class));
        assertTrue(converter.canConvert(L3.class));
        assertFalse(converter.canConvert(Object.class));
    }

    static class L1 {
    }

    static class L2 extends L1 {
    }

    static class L3 extends L2 {
    }

}