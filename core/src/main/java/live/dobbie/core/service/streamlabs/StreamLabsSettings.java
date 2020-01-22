package live.dobbie.core.service.streamlabs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JacksonParseable({"services", "streamlabs"})
public class StreamLabsSettings implements ISettingsValue {
    boolean enabled;
    @NonNull String channel;
    @NonNull String token;
    @NonNull Events events;

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Events {
        @NonNull
        final EventConfig donation;
        @NonNull
        final EventConfig loyaltyStoreRedemption;

        @Data
        @Setter(AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public static class EventConfig {
            final String destination;
            final boolean enabled;

            @JsonCreator
            public EventConfig(@JsonProperty(value = "destination") String destination,
                               @JsonProperty(value = "enabled", required = true) boolean enabled) {
                this.destination = destination;
                this.enabled = enabled;
            }
        }
    }
}
