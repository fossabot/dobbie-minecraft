package live.dobbie.core.service.streamlabs;

import com.fasterxml.jackson.annotation.JsonCreator;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JacksonParseable({"services", "streamlabs", "credentials"})
public class StreamlabsCredentials implements ISettingsValue {
    @NonNull String token;
}
