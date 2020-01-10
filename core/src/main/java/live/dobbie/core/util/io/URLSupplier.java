package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.io.mod.UnknownModSignal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@RequiredArgsConstructor
@ToString
public class URLSupplier implements IOSupplier {
    private final @NonNull URL url;

    @Override
    public @NonNull InputStream input() throws IOException {
        return url.openStream();
    }

    @Override
    public @NonNull OutputStream output() {
        throw new IllegalStateException("not supported");
    }

    @Override
    public @NonNull ModSignal getModSignal() {
        return new UnknownModSignal(); // we don't know if content was changed
    }
}
