package live.dobbie.core.context.value;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.exception.ParserRuntimeException;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import live.dobbie.core.util.Unboxing;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public interface ContextualCondition extends ContextualValue<Boolean> {

    default boolean isTrue(@NonNull ObjectContext context) throws ComputationException {
        return Unboxing.unbox(computeValue(context), false);
    }

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @ToString
    class Delegator implements ContextualCondition {
        @NonNull
        @Delegate
        ContextualValue<Boolean> contextualValue;
    }

    @RequiredArgsConstructor
    class Parser extends JsonDeserializer<ContextualCondition> {
        static String ANY_CONDITION = "any";

        private final @NonNull ScriptContextualValue.Factory<?, ?> scriptFactory;
        private final @NonNull String sourceName;
        private final @NonNull String anyCondition;
        private final @NonNull String pathSeparator;

        public Parser(@NonNull ScriptContextualValue.Factory<?, ?> scriptFactory, @NonNull String sourceName) {
            this(scriptFactory, sourceName, ANY_CONDITION, Path.SEPARATOR);
        }

        @Override
        public ContextualCondition deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonLocation location = p.getCurrentLocation();
            ObjectCodec oc = p.getCodec();
            JsonNode node = oc.readTree(p);
            if (node instanceof TextNode) {
                return fromTextNode((TextNode) node, location);
            } else if (node instanceof ObjectNode) {
                return fromObjectNode((ObjectNode) node);
            }
            throw new ParserRuntimeException("unknown conditional node: " + node);
        }

        private ContextualCondition fromTextNode(TextNode node, JsonLocation location) {
            String text = node.asText();
            if (anyCondition.equals(text)) {
                return ConstContextualValue.ALWAYS_TRUE;
            }
            return new Delegator(scriptFactory.create(node.asText(), sourceName, location.getLineNr(), Boolean.class));
        }

        private ContextualCondition fromObjectNode(ObjectNode node) {
            VarCmpContextualCondition.Builder builder = VarCmpContextualCondition.builder();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                builder.addCondition(parseKey(entry.getKey()), parseValue(entry.getValue()));
            }
            return builder.build();
        }

        private Path parseKey(String key) {
            return Path.parse(key, pathSeparator);
        }

        private Primitive parseValue(JsonNode value) {
            Primitive primitiveValue;
            if (value instanceof TextNode || value.isNull()) {
                primitiveValue = Primitive.parse(value.textValue());
            } else if (value instanceof NumericNode) {
                primitiveValue = Primitive.of(value.doubleValue());
            } else {
                throw new ParserRuntimeException("unknown primitive condition: " + value);
            }
            return primitiveValue;
        }
    }
}
