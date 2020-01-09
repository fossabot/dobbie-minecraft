package live.dobbie.core.settings.context;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.NonNull;


public interface ISettingsContext {

    <V extends ISettingsValue> V parse(@NonNull Class<V> key) throws ParserException;
}
