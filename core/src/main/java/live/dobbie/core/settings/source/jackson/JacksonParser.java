package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.JavaType;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.context.ISettingsContext;
import live.dobbie.core.settings.parser.ISettingsParser;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.util.Jackson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class JacksonParser<V extends ISettingsValue> implements ISettingsParser<JacksonNode, V> {
    private final @NonNull JavaType type;

    public JacksonParser(@NonNull Class<V> type) {
        this(constructType(type));
    }


    @Override
    public final V parse(@NonNull JacksonNode object, @NonNull ISettingsContext context) throws ParserException {
        return getTargetNode(object).getValue(type);
    }

    @NonNull
    protected abstract JacksonNode getTargetNode(JacksonNode rootNode);

    private static <T> JavaType constructType(Class<T> clazz) {
        return Jackson.getInstance().constructType(clazz);
    }

    public static class UsingPath<V extends ISettingsValue> extends JacksonParser<V> {
        private final Path path;

        public UsingPath(@NonNull JavaType type, @NonNull Path path) {
            super(type);
            this.path = path;
        }

        public UsingPath(@NonNull Class<V> type, @NonNull Path path) {
            super(type);
            this.path = path;
        }

        @Override
        protected @NonNull JacksonNode getTargetNode(JacksonNode rootNode) {
            return rootNode.getSection(path);
        }
    }

    public static class Provider implements ISettingsParser.Provider<JacksonNode> {
        @Override

        public <V extends ISettingsValue> JacksonParser<V> findParser(@NonNull Class<V> key) {
            JacksonParseable ann = key.getAnnotation(JacksonParseable.class);
            if (ann == null) {
                return null;
            }
            return new JacksonParser.UsingPath<>(key, Path.of(ann.value()));
        }
    }
}
