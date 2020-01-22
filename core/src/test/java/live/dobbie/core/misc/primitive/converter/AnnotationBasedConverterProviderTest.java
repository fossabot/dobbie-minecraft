package live.dobbie.core.misc.primitive.converter;

import live.dobbie.core.misc.primitive.BoolPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationBasedConverterProviderTest {

    @Test
    void stringTest() {
        AnnotationBasedConverterProvider provider = new AnnotationBasedConverterProvider();
        PrimitiveConverter<ToString, ? extends Primitive> parser = provider.getConverter(ToString.class);
        assertNotNull(parser);
        assertEquals(Primitive.of("hello!"), parser.parse(new ToString()));
    }

    @Test
    void definedConverterTest() {
        AnnotationBasedConverterProvider provider = new AnnotationBasedConverterProvider();
        PrimitiveConverter<ConvertableValue, ? extends Primitive> parser = provider.getConverter(ConvertableValue.class);
        assertNotNull(parser);
        assertEquals(Primitive.of(true), parser.parse(new ConvertableValue()));
    }

    @Test
    void noConverterTest() {
        AnnotationBasedConverterProvider provider = new AnnotationBasedConverterProvider();
        PrimitiveConverter<Object, ? extends Primitive> parser = provider.getConverter(Object.class);
        assertNull(parser);
    }

    @ConvertableToString
    private static class ToString {
        public String toString() {
            return "hello!";
        }
    }

    @Convertable(Converter.class)
    private static class ConvertableValue {
    }

    public static class Converter implements PrimitiveConverter<ConvertableValue, BoolPrimitive> {
        @NonNull
        @Override
        public BoolPrimitive parse(@NonNull ConvertableValue value) {
            return BoolPrimitive.TRUE;
        }
    }

}