package live.dobbie.core.service.streamelements.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/*
{ _id: '5daf497f7d4ec8577fd2e26e',
  channel: '5d8e560e916925690f63391e',
  type: 'tip',
  provider: 'twitch',
  createdAt: '2019-10-22T18:24:34.485Z',
  data:
   { tipId: '5daf4962aa930e2e25e7af2b',
     username: 'turikhay',
     amount: 1,
     currency: 'USD',
     message: 'hello world',
     items: [],
     avatar:
      'https://static-cdn.jtvnw.net/jtv_user_pictures/48e03c01-0549-4b91-b6b0-497663bc5831-profile_image-300x300.png' },
  updatedAt: '2019-10-22T18:24:34.485Z' }

 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamElementsTipEvent extends StreamElementsEvent {
    @NonNull EventData data;

    @JsonCreator
    public StreamElementsTipEvent(@JsonProperty(value = "_id", required = true) @NonNull String _id,
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
        @NonNull String tipId;
        @NonNull BigDecimal amount;
        @NonNull String currency;
        String message;
        @NonNull String username;
    }
}
