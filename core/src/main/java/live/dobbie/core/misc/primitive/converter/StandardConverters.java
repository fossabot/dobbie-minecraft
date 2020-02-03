package live.dobbie.core.misc.primitive.converter;

import live.dobbie.core.misc.primitive.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;


public class StandardConverters {
    public static final BoolConverter BOOL_CONVERTER = new BoolConverter();
    public static final NumberConverter NUMBER_CONVERTER = new NumberConverter();
    public static final StringConverter<?> STRING_CONVERTER = new StringConverter<>();
    public static final DateTimeConverter DATE_TIME_CONVERTER = new DateTimeConverter();

    public static class BoolConverter implements PrimitiveConverter<Boolean, BoolPrimitive> {
        @NonNull
        @Override
        public BoolPrimitive parse(@NonNull Boolean value) {
            return new BoolPrimitive(value);
        }
    }

    public static class NumberConverter implements PrimitiveConverter<Number, NumberPrimitive> {
        private static final NumberConverter instance = new NumberConverter();

        public static <IN extends Number> PrimitiveConverter<IN, NumberPrimitive> instance() {
            return (PrimitiveConverter<IN, NumberPrimitive>) instance;
        }

        @NonNull
        @Override
        public NumberPrimitive parse(@NonNull Number value) {
            return new NumberPrimitive(value);
        }
    }

    public static class StringConverter<IN> implements PrimitiveConverter<IN, StringPrimitive> {
        private static final StringConverter<?> instance = new StringConverter<>();

        public static <IN> PrimitiveConverter<IN, StringPrimitive> instance() {
            return (PrimitiveConverter<IN, StringPrimitive>) instance;
        }

        @NonNull
        @Override
        public StringPrimitive parse(@NonNull IN value) {
            return new StringPrimitive(value.toString());
        }
    }

    public static class DateTimeConverter implements PrimitiveConverter<Instant, DateTimePrimitive> {
        @NonNull
        @Override
        public DateTimePrimitive parse(@NonNull Instant value) {
            return new DateTimePrimitive(value);
        }
    }

    @RequiredArgsConstructor
    public static class NullAwareConverter<IN> implements PrimitiveConverter<IN, Primitive> {
        private final PrimitiveConverter<IN, ?> converter;

        @NonNull
        @Override
        public Primitive parse(IN value) {
            return value == null ? NullPrimitive.INSTANCE : converter.parse(value);
        }
    }

}
