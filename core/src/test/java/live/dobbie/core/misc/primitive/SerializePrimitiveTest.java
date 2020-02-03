package live.dobbie.core.misc.primitive;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializePrimitiveTest {

    @Test
    void serializeBool() throws IOException, ClassNotFoundException {
        testSerialize(new BoolPrimitive(false));
        testSerialize(new BoolPrimitive(true));
    }

    @Test
    void serializeDateTime() throws IOException, ClassNotFoundException {
        testSerialize(new DateTimePrimitive(Instant.now()));
        testSerialize(new DateTimePrimitive(Instant.ofEpochMilli(0)));
    }

    @Test
    void serializeNull() throws IOException, ClassNotFoundException {
        testSerialize(NullPrimitive.INSTANCE);
    }

    @Test
    void serializeNumber() throws IOException, ClassNotFoundException {
        testSerialize(new NumberPrimitive(1337));
        testSerialize(new NumberPrimitive(1337L));
        testSerialize(new NumberPrimitive(1337.));
        testSerialize(new NumberPrimitive(1337.f));
        testSerialize(new NumberPrimitive(new BigDecimal("1337")));
    }

    @Test
    void serializeString() throws IOException, ClassNotFoundException {
        testSerialize(new StringPrimitive("foo"));
        testSerialize(new StringPrimitive("bar"));
    }

    private void testSerialize(Primitive value) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        objectOutputStream.close();

        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object deserializedPrimitive = objectInputStream.readObject();

        assertEquals(value, deserializedPrimitive);
        assertEquals(value.hashCode(), deserializedPrimitive.hashCode());
    }

}