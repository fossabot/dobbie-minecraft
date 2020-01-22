package live.dobbie.core.service.streamelements.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamElementsLoyaltyStoreRedemptionEvent extends StreamElementsEvent {
    @NonNull EventData data;

    @JsonCreator
    public StreamElementsLoyaltyStoreRedemptionEvent(@JsonProperty(value = "_id", required = true) @NonNull String _id,
                                                     @JsonProperty(value = "channel", required = true) @NonNull String channel,
                                                     @JsonProperty(value = "type", required = true) @NonNull String type,
                                                     @JsonProperty(value = "data", required = true) @NonNull EventData data) {
        super(_id, channel, type);
        this.data = data;
    }


    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventData {
        @NonNull BigDecimal amount;
        @NonNull String username;
        @NonNull String redemption;
        String message;
    }
}
