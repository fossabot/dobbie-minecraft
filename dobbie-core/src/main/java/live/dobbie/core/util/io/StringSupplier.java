package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.io.mod.ObjectModSignal;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@ToString
public class StringSupplier implements IOSupplier {
    @NonNull
    @Getter
    private String str;
    @NonNull
    private final Charset charset;

    public StringSupplier(@NonNull String str) {
        this(str, StandardCharsets.UTF_8);
    }

    @Override
    public @NonNull InputStream input() {
        return new ByteArrayInputStream(str.getBytes(charset));
    }

    @Override
    public @NonNull OutputStream output() {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                str = new String(toByteArray(), charset);
            }
        };
    }

    @Override
    public @NonNull ModSignal getModSignal() {
        return new ObjectModSignal(str, charset);
    }
}
