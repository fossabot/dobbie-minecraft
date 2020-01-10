package live.dobbie.core.settings.parser;

import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequentialSettingsParserProvider<O extends ISettingsObject> implements ISettingsParser.Provider<O> {
    private final @NonNull List<ISettingsParser.Provider<O>> providers;

    @Override
    public <V extends ISettingsValue> ISettingsParser<O, V> findParser(@NonNull Class<V> key) {
        for (ISettingsParser.Provider<O> provider : providers) {
            ISettingsParser<O, V> parser = provider.findParser(key);
            if (parser != null) {
                return parser;
            }
        }
        return null;
    }

    public static <O extends ISettingsObject> Builder<O> builder(Class<O> type) {
        return new Builder<>();
    }

    public static <O extends ISettingsObject> Builder<O> builder() {
        return builder(null);
    }

    public static class Builder<O extends ISettingsObject> {
        private final List<ISettingsParser.Provider<O>> providers = new ArrayList<>();

        public Builder<O> registerProvider(@NonNull ISettingsParser.Provider<O> provider) {
            providers.add(provider);
            return this;
        }

        public SequentialSettingsParserProvider<O> build() {
            return new SequentialSettingsParserProvider<>(new ArrayList<>(providers));
        }
    }
}
