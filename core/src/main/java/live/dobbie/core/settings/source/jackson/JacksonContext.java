package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = {"objectMapper", "modCount"})
class JacksonContext {
    @NonNull
    @Getter
    private final ObjectMapper objectMapper;
    @NonNull
    @Getter
    private ObjectNode rootNode;
    @Getter
    private long modCount;

    public JacksonContext(@NonNull ObjectMapper objectMapper) {
        this(objectMapper, objectMapper.createObjectNode());
    }

    public JacksonContext() {
        this(new ObjectMapper());
    }

    public boolean didMod() {
        return modCount != 0;
    }

    public void modIncrement() {
        modCount++;
    }

    public void modClear() {
        modCount = 0;
    }
}
