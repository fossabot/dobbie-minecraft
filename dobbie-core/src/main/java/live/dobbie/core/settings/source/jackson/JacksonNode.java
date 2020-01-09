package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.object.section.ISettingsSection;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// TODO cache
@EqualsAndHashCode(of = {"context", "path"})
public class JacksonNode implements ISettingsSection {
    @NonNull
    @Getter(AccessLevel.PACKAGE)
    private final JacksonContext context;
    @NonNull
    @Getter
    private final Path path;

//    private JsonNode cachedNode;
//    private JavaType cachedClass;
//    private Object cachedValue;
//    private int modCount;

    public JacksonNode(@NonNull JacksonContext context, @NonNull Path path) {
        this.context = context;
        this.path = path;
    }

    public JacksonNode() {
        this(new JacksonContext(), Path.EMPTY);
    }

    public JsonNode traverse() {
        return traverse(context.getRootNode(), path);
    }

    public <T> T getValue(@NonNull JavaType type) throws ParserException {
        JsonNode node = null;
        T value;
        try {
            node = traverse();
            value = context.getObjectMapper().convertValue(node, type);
        } catch (RuntimeException e) {
            Throwable cause = e;
            if (e.getCause() instanceof JsonProcessingException) {
                cause = e.getCause();
            }
            throw new ParserException("could not parse " + node + " into " + type, cause);
        }
        return value;
    }

    @Override
    public @NonNull JacksonNode getSection(@NonNull Path path) {
        return new JacksonNode(context, this.path.merge(path));
    }

    @Override
    public <T> T getValue(@NonNull Class<T> type) throws ParserException {
        return getValue(context.getObjectMapper().constructType(type));
    }

    @Override
    public void setValue(@NonNull Class type, Object value) throws ParserException {
        traverseAndSet(context.getObjectMapper(), context.getRootNode(), path, value);
        context.modIncrement();
    }

    @Override
    public boolean exists() {
        return traverse() != null;
    }

    @Override
    public boolean isEmpty() throws ParserException {
        ObjectNode node = ensureObjectNode();
        return !node.fieldNames().hasNext();
    }

    @Override
    public @NonNull List<String> getList() throws ParserException {
        return getValue(TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }

    @Override
    public @NonNull Set<String> getKeys() throws ParserException {
        ObjectNode node = ensureObjectNode();
        Set<String> keys = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(keys::add);
        return keys;
    }

    private ObjectNode ensureObjectNode() throws ParserException {
        JsonNode node = traverse();
        if (!(node instanceof ObjectNode)) {
            throw new ParserException(Path.toString(path) + " is not a ObjectNode");
        }
        return (ObjectNode) node;
    }


    static JsonNode traverse(@NonNull ObjectNode rootNode, @NonNull Path path) {
        List<String> breadcrumbs = path.getBreadcrumbs();
        JsonNode node = rootNode;
        for (String key : breadcrumbs) {
            node = node.get(key);
            if (node == null) {
                break;
            }
        }
        return node;
    }

    static void traverseAndSet(@NonNull ObjectMapper objectMapper, @NonNull ObjectNode rootNode, @NonNull Path path, Object value) throws ParserException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("cannot set root path");
        }
        List<String> breadcrumbs = path.getBreadcrumbs();
        ObjectNode objectNode = rootNode;
        JsonNode currentNode = objectNode;
        for (int i = 0; i < breadcrumbs.size(); i++) {
            String key = breadcrumbs.get(i);
            if (currentNode instanceof ObjectNode) {
                objectNode = (ObjectNode) currentNode;
                if (objectNode.has(key)) {
                    currentNode = objectNode.get(key);
                } else {
                    ObjectNode newNode = objectMapper.createObjectNode();
                    objectNode.set(key, newNode);
                    currentNode = newNode;
                }
            } else {
                throw new ParserException("cannot set " + Path.toString(path, i) + " because it is not an ObjectNode");
            }
        }
        objectNode.set(breadcrumbs.get(breadcrumbs.size() - 1), objectMapper.valueToTree(value));
    }
}
