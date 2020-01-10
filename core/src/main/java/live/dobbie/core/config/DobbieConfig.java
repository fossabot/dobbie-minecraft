package live.dobbie.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class DobbieConfig implements ISettingsValue {
    Timer timer;

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    public static class Timer implements ISettingsValue {
        Ticks ticks;

        @Value
        @AllArgsConstructor(onConstructor = @__(@JsonCreator))
        @JacksonParseable({"timer", "ticks"})
        public static class Ticks implements ISettingsValue {
            public static final Ticks DEFAULT = new Ticks(1, 5);
            int timeoutBetween, reloadEvery;
        }
    }
}
