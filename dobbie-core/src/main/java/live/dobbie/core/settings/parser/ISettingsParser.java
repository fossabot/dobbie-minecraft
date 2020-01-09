package live.dobbie.core.settings.parser;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.context.ISettingsContext;
import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.NonNull;


public interface ISettingsParser<O extends ISettingsObject, V extends ISettingsValue> {
    V parse(@NonNull O source, ISettingsContext context) throws ParserException;

    interface Provider<O extends ISettingsObject> {
        <V extends ISettingsValue> ISettingsParser<O, V> findParser(@NonNull Class<V> key);

        @NonNull
        default <V extends ISettingsValue> ISettingsParser<O, V> requireParser(@NonNull Class<V> key) {
            ISettingsParser<O, V> parser = findParser(key);
            if (parser == null) {
                throw new IllegalArgumentException("could not find parser: " + key);
            }
            return parser;
        }
    }
}
