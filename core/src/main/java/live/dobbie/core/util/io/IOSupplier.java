package live.dobbie.core.util.io;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public interface IOSupplier extends InputSupplier {
    @NonNull OutputStream output() throws IOException;
}
