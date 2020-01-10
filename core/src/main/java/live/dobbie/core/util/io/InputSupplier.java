package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.ModSignal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.io.IOException;
import java.io.InputStream;

public interface InputSupplier {
    @NonNull InputStream input() throws IOException;

    @NonNull ModSignal getModSignal();

    @RequiredArgsConstructor
    class Delegated implements InputSupplier {
        private final @NonNull
        @Delegate
        InputSupplier inputSupplier;
    }
}
