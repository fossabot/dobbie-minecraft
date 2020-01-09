package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.io.mod.ObjectModSignal;
import lombok.NonNull;

import java.io.*;

public class MemorySupplier implements IOSupplier {
    @NonNull
    private byte[] buffer;

    public MemorySupplier(@NonNull byte[] buffer) {
        this.buffer = buffer.clone();
    }

    @Override
    public @NonNull InputStream input() {
        return new ByteArrayInputStream(buffer);
    }

    @Override
    public @NonNull OutputStream output() {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                buffer = toByteArray();
            }
        };
    }

    @Override
    public @NonNull ModSignal getModSignal() {
        return new ObjectModSignal(buffer);
    }
}
