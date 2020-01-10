package live.dobbie.core.script.js.converter;

import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.Collections;

import static org.mockito.Mockito.*;

class TypedValueConverterTest {

    @Test
    void fromJStest() {
        JSValueConverter fallbackConverter = mock(JSValueConverter.class);
        //Scriptable scope = mock(Scriptable.class);
        TypedFromJSConverter l1Converter = mock(TypedFromJSConverter.class);
        TypedValueConverter converter = new TypedValueConverter(
                Collections.singletonMap(L1.class, l1Converter),
                Collections.emptyMap(),
                fallbackConverter
        );

        Object l1Object = new NamedObject("l1");
        converter.fromJs(l1Object, L1.class);
        verify(l1Converter).typedFromJs(eq(l1Object));
        verify(fallbackConverter, times(0)).fromJs(eq(l1Object), eq(L1.class));

        Object l2Object = new NamedObject("l2");
        converter.fromJs(l2Object, L2.class);
        verify(l1Converter).typedFromJs(eq(l2Object));
        verify(fallbackConverter, times(0)).fromJs(eq(l2Object), eq(L2.class));

        Object l3Object = new NamedObject("l3");
        converter.fromJs(l3Object, L3.class);
        verify(l1Converter).typedFromJs(eq(l3Object));
        verify(fallbackConverter, times(0)).fromJs(eq(l3Object), eq(L3.class));
    }

    @Test
    void toJStest() {
        JSValueConverter fallbackConverter = mock(JSValueConverter.class);
        Scriptable scope = mock(Scriptable.class);
        Context context = mock(Context.class);
        ToJSConverter l1Converter = mock(ToJSConverter.class);
        TypedValueConverter converter = new TypedValueConverter(
                Collections.emptyMap(),
                Collections.singletonMap(L1.class, l1Converter),
                fallbackConverter
        );

        L3 l3Object = new L3();
        converter.toJs(l3Object, scope, context);
        verify(l1Converter).toJs(eq(l3Object), eq(scope), eq(context));

        L2 l2Object = new L2();
        converter.toJs(l2Object, scope, context);
        verify(l1Converter).toJs(eq(l2Object), eq(scope), eq(context));

        L1 l1Object = new L1();
        converter.toJs(l1Object, scope, context);
        verify(l1Converter).toJs(eq(l1Object), eq(scope), eq(context));
    }

    static class L1 {
    }

    static class L2 extends L1 {
    }

    static class L3 extends L2 {
    }

    @Value
    static class NamedObject {
        @NonNull String name;
    }

}