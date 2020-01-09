package live.dobbie.core.script;

import live.dobbie.core.misc.Text;
import live.dobbie.core.util.io.InputSupplier;
import live.dobbie.core.util.io.StringSupplier;
import live.dobbie.core.util.io.URLSupplier;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
public class ScriptSource {
    @NonNull InputSupplier inputSupplier;
    @NonNull Charset charset;
    @NonNull String sourceName;
    @NonNull URI uri;
    int sourceLine;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptSource that = (ScriptSource) o;
        return sourceLine == that.sourceLine &&
                inputSupplier.getModSignal().equals(that.inputSupplier.getModSignal()) &&
                charset.equals(that.charset) &&
                sourceName.equals(that.sourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputSupplier.getModSignal(), charset, sourceName, sourceLine);
    }

    public InputStreamReader newReader() throws IOException {
        return new InputStreamReader(inputSupplier.input(), charset);
    }

    public static ScriptSource fromString(@NonNull String str, @NonNull String sourceName, int sourceLine) {
        return new ScriptSource(new StringSupplier(str, StandardCharsets.UTF_8), StandardCharsets.UTF_8, sourceName, toInternalURI(sourceName, sourceLine), sourceLine);
    }

    public static ScriptSource fromString(@NonNull String str, @NonNull String sourceName) {
        return fromString(str, sourceName, 0);
    }

    public static ScriptSource fromText(@NonNull Text text) {
        return fromString(text.getString(), text.getSourceName(), text.getLineNumber());
    }

    public static ScriptSource fromURL(@NonNull URL url, @NonNull Charset charset) {
        return new ScriptSource(new URLSupplier(url), charset, url.toExternalForm(), toURI(url), 0);
    }

    private static URI toInternalURI(String name, int sourceLine) {
        try {
            return new URI("internal://" + URLEncoder.encode(name, "UTF-8") + "#L" + sourceLine);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new RuntimeException("could not create fake URI from \"" + name + "\"", e);
        }
    }

    private static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("could not convert URL to URI: " + url.toExternalForm(), e);
        }
    }
}
