package live.dobbie.core.settings.parser;

import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SettingsParserProvider<O extends ISettingsObject> implements ISettingsParser.Provider<O> {
    private final @NonNull Map<Class, ISettingsParser> map;

    @Override
    public @NonNull <V extends ISettingsValue> ISettingsParser<O, V> findParser(@NonNull Class<V> key) {
        return map.get(key);
    }

    public static <O extends ISettingsObject> Builder builder(Class<O> type) {
        return new Builder<O>();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Builder<O extends ISettingsObject> {
        private final Map<Class, ISettingsParser> map = new HashMap<>();

        public <V extends ISettingsValue> Builder<O> registerParser(@NonNull Class<V> key,
                                                                    @NonNull ISettingsParser<O, V> parser) {
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("already registered: " + key);
            }
            map.put(key, parser);
            return this;
        }

        public SettingsParserProvider<O> build() {
            return new SettingsParserProvider<>(new HashMap<>(map));
        }
    }
}
