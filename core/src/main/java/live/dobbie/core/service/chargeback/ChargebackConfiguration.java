package live.dobbie.core.service.chargeback;

import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.Value;

@Value
@JacksonParseable({"services", "chargeback"})
public class ChargebackConfiguration implements ISettingsValue {
    boolean enabled;
}
