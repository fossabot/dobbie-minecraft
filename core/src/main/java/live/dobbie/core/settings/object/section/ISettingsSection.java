package live.dobbie.core.settings.object.section;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.util.Unboxing;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

public interface ISettingsSection extends ISettingsObject {
    @NonNull Path getPath();

    @NonNull ISettingsSection getSection(@NonNull Path path);

    <T> T getValue(@NonNull Class<T> type) throws ParserException;

    void setValue(@NonNull Class type, Object value) throws ParserException;

    boolean exists();

    boolean isEmpty() throws ParserException;

    @NonNull List<String> getList() throws ParserException;

    @NonNull Set<String> getKeys() throws ParserException;

    default ISettingsSection requireExist() throws ParserException {
        if (!exists()) {
            throw new ParserException("required, but does not exist: " + getPath());
        }
        return this;
    }

    default ISettingsSection getSection(@NonNull String... path) {
        return getSection(Path.of(path));
    }

    default Object getValue() throws ParserException {
        return getValue(Object.class);
    }

    default String getString() throws ParserException {
        return getValue(String.class);
    }

    default boolean getBoolean() throws ParserException {
        return Unboxing.unbox(getValue(boolean.class));
    }

    default int getInteger() throws ParserException {
        return Unboxing.unbox(getValue(int.class));
    }

    default double getDouble() throws ParserException {
        return Unboxing.unbox(getValue(double.class));
    }

    default <T> void setValue(T value) throws ParserException {
        if (value == null) {
            setValue(Object.class, null);
        } else {
            setValue(value.getClass(), value);
        }
    }
}
