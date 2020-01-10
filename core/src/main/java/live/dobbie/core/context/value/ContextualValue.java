package live.dobbie.core.context.value;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.exception.ComputationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

public interface ContextualValue<V> {
    @NonNull V computeValue(@NonNull ObjectContext context) throws ComputationException;

    @RequiredArgsConstructor
    class Parser extends JsonDeserializer<ContextualValue> {
        private final @NonNull ScriptContextualValue.Parser<?, ?, ?> parser;

        @Override
        public ContextualValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return parser.deserialize(p, ctxt);
        }
    }
}
