package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JacksonNodeTest {
    @Test
    void basicTest() throws ParserException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode o = objectMapper.createObjectNode();
        o.set("foo", new TextNode("bar"));

        JacksonContext context = new JacksonContext(objectMapper, o);
        JacksonNode node = new JacksonNode(context, Path.EMPTY);

        assertEquals("bar", o.get("foo").asText());
        assertEquals("bar", node.getSection("foo").getValue());
        assertEquals(0, context.getModCount());
    }

    @Test
    void noValueTest() throws ParserException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode o = objectMapper.createObjectNode();
        o.set("foo", new TextNode("bar"));

        JacksonContext context = new JacksonContext(objectMapper, o);
        JacksonNode node = new JacksonNode(context, Path.EMPTY);

        assertNull(node.getSection("foo1").getValue());
    }

    @Test
    void basicInheritTest() throws ParserException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode inherit = objectMapper.createObjectNode();
        inherit.set("foo1", new TextNode("bar"));
        root.set("foo0", inherit);

        JacksonContext context = new JacksonContext(objectMapper, root);
        JacksonNode node = new JacksonNode(context, Path.EMPTY);

        assertEquals("bar", inherit.get("foo1").asText());
        assertEquals("bar", node.getSection("foo0", "foo1").getValue());
        assertEquals(0, context.getModCount());
    }

    @Test
    void basicSetTest() throws ParserException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode o = objectMapper.createObjectNode();
        o.set("foo", new TextNode("bar"));

        JacksonContext context = new JacksonContext(objectMapper, o);
        JacksonNode node = new JacksonNode(context, Path.EMPTY);
        node.getSection("foo").setValue("bar1");

        assertEquals("bar1", o.get("foo").asText());
        assertEquals("bar1", node.getSection("foo").getString());
        assertEquals(1, context.getModCount());
    }

    @Test
    void nonExistingSectionSetTest() throws ParserException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode o = objectMapper.createObjectNode();
        o.set("foo", new TextNode("bar"));

        JacksonContext context = new JacksonContext(objectMapper, o);
        JacksonNode node = new JacksonNode(context, Path.EMPTY);
        node.getSection("test").setValue("value");

        assertEquals("value", o.get("test").asText());
        assertEquals("value", node.getSection("test").getString());
        assertEquals(1, context.getModCount());
    }

    @Test
    void rootSetTest() {
        JacksonNode node = new JacksonNode();
        Assertions.assertThrows(IllegalArgumentException.class, () -> node.setValue("bar1"));
        assertEquals(0, node.getContext().getModCount());
    }
}