package live.dobbie.core.substitutor.plain;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface VarConverter {
    String convertVarValue(String value);

    class Identity implements VarConverter {
        public static final Identity INSTANCE = new Identity();

        @Override
        public String convertVarValue(String value) {
            return value;
        }
    }

    @RequiredArgsConstructor
    class JsonEscaping implements VarConverter {
        public static final JsonEscaping INSTANCE = new JsonEscaping();

        private final @NonNull JsonStringEncoder jsonStringEncoder = new JsonStringEncoder();

        @Override
        public String convertVarValue(String value) {
            if (value == null) {
                return null;
            }
            StringBuilder b = new StringBuilder();
            jsonStringEncoder.quoteAsString(value, b);
            return b.toString();
        }
    }

    @RequiredArgsConstructor
    class DoubleJsonEscaping implements VarConverter {
        public static final DoubleJsonEscaping INSTANCE = new DoubleJsonEscaping(new JsonEscaping());

        private final @NonNull JsonEscaping escaping;

        @Override
        public String convertVarValue(String value) {
            return escaping.convertVarValue(escaping.convertVarValue(value));
        }
    }
}
