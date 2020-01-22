package live.dobbie.core.misc.primitive.storage;

import com.google.common.collect.ImmutableMap;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.path.Path;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static live.dobbie.core.misc.primitive.Primitive.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class StorageAwareObjectContextTest {

    @Test
    void basicTest() {
        Object o = new Object();
        ObjectContext delegate = mock(ObjectContext.class);
        when(delegate.getObjects()).thenReturn(Collections.singletonMap("a", o));
        when(delegate.getObject(notNull())).thenCallRealMethod();
        when(delegate.getVariables()).thenReturn(Collections.emptyMap());
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        when(storage.getVariables()).thenReturn(Collections.emptyMap());
        when(storage.getVariable(eq(Path.of("foo")))).thenReturn(of("bar"));
        StorageAwareObjectContext context = new StorageAwareObjectContext(delegate, storage);
        assertEquals(o, context.getObject("a"));
        assertEquals(of("bar"), context.getVariable(Path.of("foo")));
    }

    @Test
    void mergeVarsTest() {
        ObjectContext delegate = mock(ObjectContext.class);
        when(delegate.getVariables()).thenReturn(ImmutableMap.of(
                Path.of("foo"), of("bar"),
                Path.of("foo0"), of("bar0"),
                Path.of("foo1"), of("bar1")
        ));
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        when(storage.getVariables()).thenReturn(ImmutableMap.of(
                Path.of("foo0"), of("bar00"),
                Path.of("foo1"), of("bar11")
        ));
        StorageAwareObjectContext context = new StorageAwareObjectContext(delegate, storage);
        Map<Path, Primitive> vars = context.getVariables();
        assertNotNull(vars);
        assertEquals(3, vars.size());
        assertEquals(of("bar00"), vars.get(Path.of("foo0")));
        assertEquals(of("bar11"), vars.get(Path.of("foo1")));
        assertEquals(of("bar"), vars.get(Path.of("foo")));
    }

    @Test
    void mergeObjectTest() {
        ObjectContext delegate = mock(ObjectContext.class);
        when(delegate.getObjects()).thenReturn(Collections.emptyMap());
        MutablePrimitiveStorage storage = mock(MutablePrimitiveStorage.class);
        StorageAwareObjectContext context = new StorageAwareObjectContext(delegate, storage, "storage");
        Map<String, Object> objs = context.getObjects();
        assertEquals(storage, objs.get("storage"));
    }

}