package live.dobbie.core.util.io;

import live.dobbie.core.util.io.mod.LongModSignal;
import live.dobbie.core.util.io.mod.ModSignal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class FileSupplier implements IOSupplier {
    private final @NonNull File file;
    private final boolean createIfNotExist;

    public FileSupplier(@NonNull File file) {
        this(file, true);
    }

    @Override
    public @NonNull InputStream input() throws IOException {
        createIfNotExist();
        return new FileInputStream(file);
    }

    @Override
    public @NonNull OutputStream output() throws IOException {
        createIfNotExist();
        return new FileOutputStream(file);
    }

    @Override
    public @NonNull ModSignal getModSignal() {
        return new LongModSignal(file, file.lastModified());
    }

    private void createIfNotExist() throws IOException {
        if (createIfNotExist) {
            if (!file.isFile()) {
                if (!file.createNewFile()) {
                    throw new FileNotFoundException(file.getAbsolutePath() + "(createNewFile returned false)");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "FileSupplier{" +
                "file=" + file.getAbsolutePath() +
                '}';
    }
}
