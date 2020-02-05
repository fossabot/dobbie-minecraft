package live.dobbie.core.misc.primitive;

import live.dobbie.core.exception.ParserRuntimeException;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.time.Instant;

public interface Primitive extends Serializable {
    Object getValue();

    @NonNull
    static NullPrimitive ofNull() {
        return NullPrimitive.INSTANCE;
    }

    @NonNull
    static StringPrimitive of(@NonNull String string) {
        return new StringPrimitive(string);
    }

    @NonNull
    static BoolPrimitive of(boolean bool) {
        return bool ? BoolPrimitive.TRUE : BoolPrimitive.FALSE;
    }

    @NonNull
    static BoolPrimitive of(@NonNull Boolean bool) {
        return bool ? BoolPrimitive.TRUE : BoolPrimitive.FALSE;
    }

    @NonNull
    static NumberPrimitive of(@NonNull Number number) {
        return new NumberPrimitive(number);
    }

    @NonNull
    static DateTimePrimitive of(@NonNull Instant instant) {
        return new DateTimePrimitive(instant);
    }

    @NonNull
    static Primitive of(Object object) throws ParserRuntimeException {
        if (object == null) {
            return NullPrimitive.INSTANCE;
        }
        if (object instanceof Primitive) {
            return (Primitive) object;
        }
        if (object instanceof String) {
            return of((String) object);
        }
        if (object instanceof Boolean) {
            return of((Boolean) object);
        }
        if (object instanceof Number) {
            return of((Number) object);
        }
        if (object instanceof Instant) {
            return of((Instant) object);
        }
        throw new ParserRuntimeException("object of following class is not a primitive: " + object.getClass());
    }

    @NonNull
    static Primitive parse(String val) {
        if (val == null) {
            return ofNull();
        }
        if ("true".equals(val)) {
            return of(true);
        } else if ("false".equals(val)) {
            return of(false);
        }
        try {
            return of(NumberUtils.createNumber(val));
        } catch (NumberFormatException ignored) {
        }
        return of(val);
    }

    static int compare(@NonNull Primitive p0, @NonNull Primitive p1) {
        if (p0 instanceof NumberPrimitive && p1 instanceof NumberPrimitive) {
            return ((NumberPrimitive) p0).compareTo((NumberPrimitive) p1);
        }
        if (p0 instanceof DateTimePrimitive && p1 instanceof DateTimePrimitive) {
            return ((DateTimePrimitive) p0).compareTo((DateTimePrimitive) p1);
        }
        throw new IllegalArgumentException(
                "cannot compare primitives of two different or incomparable types;" +
                        " p0: " + p0 + ", p1: " + p1
        );
    }

    static Object toObject(Primitive primitive) {
        if (primitive == null || primitive instanceof NullPrimitive) {
            return null;
        }
        return primitive.getValue();
    }


    static String toString(Primitive primitive) {
        Object value = toObject(primitive);
        return value == null ? null : String.valueOf(value);
    }
} 
