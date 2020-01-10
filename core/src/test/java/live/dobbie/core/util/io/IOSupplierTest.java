package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IOSupplierTest {

    @Test
    void memorySupplierTest() throws IOException {
        MemorySupplier supplier = new MemorySupplier(new byte[0]);
        testSupplier(supplier);
    }

    @Test
    void stringSupplierTest() throws IOException {
        StringSupplier supplier = new StringSupplier("foo");
        testSupplier(supplier);
    }

    @Test
    void urlSupplierTest() throws IOException {
        URLSupplier supplier = new URLSupplier(new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        ModSignal modSignal0 = supplier.getModSignal();
        ModSignal modSignal1 = supplier.getModSignal();
        assertNotEquals(modSignal0, modSignal1);
    }

    private void testSupplier(IOSupplier supplier) throws IOException {
        ModSignal modSignal0 = supplier.getModSignal();
        ModSignal modSignal1 = supplier.getModSignal();
        assertEquals(modSignal0, modSignal1);
        try (OutputStream out = supplier.output()) {
            // do anything
            out.flush();
        }
        ModSignal modSignal2 = supplier.getModSignal();
        assertNotEquals(modSignal0, modSignal2);
    }
}