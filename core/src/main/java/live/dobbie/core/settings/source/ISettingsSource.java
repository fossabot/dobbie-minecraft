package live.dobbie.core.settings.source;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.ISettingsObject;
import lombok.NonNull;

import java.io.IOException;

public interface ISettingsSource<O extends ISettingsObject> {
    @NonNull O getObject();

    void load() throws IOException, ParserException;

    void save() throws IOException, ParserException;
}
