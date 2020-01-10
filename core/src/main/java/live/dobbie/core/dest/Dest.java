package live.dobbie.core.dest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
@JsonDeserialize(using = Dest.Deserializer.class)
public class Dest {
    @NonNull String name;
    @NonNull Map<String, DestSection> sections;


    public DestSection getSection(@NonNull String name) {
        return sections.get(name);
    }

    @NonNull
    public DestSection requireSection(@NonNull String name) {
        DestSection destSection = getSection(name);
        if (destSection == null) {
            throw new IllegalArgumentException("section \"" + name + "\" not found in dest " + name);
        }
        return destSection;
    }

    public static class Deserializer extends JsonDeserializer<Dest> {
        @Override
        public Dest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            ObjectNode node = codec.readTree(p);
            String name = Validate.notNull(
                    Validate.notNull(node.get("name"), "name missing").textValue(),
                    "name is not a text"
            );
            List<DestSection> destSectionList = codec.readValue(
                    codec.treeAsTokens(Validate.notNull(node.get("sections"), "missing sections")),
                    ctxt.getTypeFactory().constructCollectionLikeType(List.class, DestSection.class)
            );
            return new Dest(name, extractNamesIntoMap(destSectionList));
        }

        @NonNull
        private static Map<String, DestSection> extractNamesIntoMap(List<DestSection> list) {
            return Collections.unmodifiableMap(list
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    DestSection::getName,
                                    Function.identity(),
                                    throwingMerger(),
                                    LinkedHashMap::new
                            )
                    )
            );
        }

        // Collectors.throwingMerger()
        private static <T> BinaryOperator<T> throwingMerger() {
            return (u, v) -> {
                throw new IllegalStateException(String.format("Duplicate key %s", u));
            };
        }
    }
}
