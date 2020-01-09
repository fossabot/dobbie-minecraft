package live.dobbie.core.misc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;

@Value
@EqualsAndHashCode(exclude = "delta")
@JsonDeserialize(using = Range.JacksonParser.class)
public class Range implements Comparable<Range> {
    double min, max, delta;

    public Range(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        this.min = min;
        this.max = max;
        this.delta = max - min;
    }

    public Range(double value) {
        this(value, value);
    }

    @Override
    public int compareTo(@NonNull Range o) {
        return Double.compare(delta, o.delta);
    }

    public static Range parse(@NonNull String value) {
        Range range;
        if (value.contains("..")) {
            // range
            String[] doubles = value.split("\\.\\.");
            range = new Range(Double.parseDouble(doubles[0]), Double.parseDouble(doubles[1]));
        } else {
            range = new Range(Double.parseDouble(value));
        }
        return range;
    }

    public static class JacksonParser extends JsonDeserializer<Range> {
        @Override
        public Range deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return parse(p.getValueAsString());
        }
    }
}
