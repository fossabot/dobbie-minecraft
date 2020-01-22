package live.dobbie.core.misc.primitive.converter;

import live.dobbie.core.misc.primitive.DateTimePrimitive;
import live.dobbie.core.misc.primitive.NumberPrimitive;
import lombok.NonNull;

import java.time.Instant;

public class DateTimeConverters {
    public static final ToDateTime TO_DATE_TIME = new ToDateTime();
    public static final InstantToMillis INSTANT_TO_MILLIS = new InstantToMillis();

    public static class ToDateTime implements PrimitiveConverter<Instant, DateTimePrimitive> {
        @NonNull
        @Override
        public DateTimePrimitive parse(@NonNull Instant value) {
            return new DateTimePrimitive(value);
        }
    }

    public static class InstantToMillis implements PrimitiveConverter<Instant, NumberPrimitive> {
        @NonNull
        @Override
        public NumberPrimitive parse(@NonNull Instant value) {
            return new NumberPrimitive(value.toEpochMilli());
        }
    }
}
