package live.dobbie.core.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class Unboxing {
    public boolean unbox(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    public boolean unbox(Boolean value) {
        return unbox(value, false);
    }

    public int unbox(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    public int unbox(Integer value) {
        return unbox(value, 0);
    }

    public double unbox(Double value, double defaultValue) {
        return value == null ? defaultValue : value;
    }

    public double unbox(Double value) {
        return unbox(value, 0.);
    }
}
