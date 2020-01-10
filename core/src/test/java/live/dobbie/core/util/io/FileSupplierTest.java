package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FileSupplierTest {

    @TempDir
    Path tempDir;

    @Test
    void readTest() throws IOException {
        File tempFile = tempDir.resolve("readTest.txt").toFile();
        FileUtils.write(tempFile, "hello, world!", StandardCharsets.UTF_8);

        FileSupplier supplier = new FileSupplier(tempFile);
        ModSignal modCounter0 = supplier.getModSignal();

        try (InputStream inputStream = supplier.input()) {
            assertEquals("hello, world!", IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
    }

    @Test
    void writeTest() throws IOException {
        File tempFile = tempDir.resolve("writeTest.txt").toFile();
        FileSupplier supplier = new FileSupplier(tempFile);
        try (OutputStream outputStream = supplier.output()) {
            IOUtils.write("foo", outputStream, StandardCharsets.UTF_8);
        }
        assertEquals("foo", FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8));
    }

    @Test
    void modTest() throws IOException {
        File tempFile = tempDir.resolve("modTest.txt").toFile();
        FileSupplier supplier = new FileSupplier(tempFile);
        ModSignal modCounter0 = supplier.getModSignal();
        ModSignal modCounter1 = supplier.getModSignal();
        assertEquals(modCounter0, modCounter1);
        FileUtils.write(tempFile, "modified!", StandardCharsets.UTF_8);
        ModSignal modCounter2 = supplier.getModSignal();
        assertNotEquals(modCounter0, modCounter2);
    }

}