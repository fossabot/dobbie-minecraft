package live.dobbie.core.service.twitch.event;

import com.github.twitch4j.common.events.TwitchEvent;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class CustomTwitchChannelEvent extends TwitchEvent {
    private final @NonNull TwitchChannel channel;

    protected boolean canEqual(Object other) {
        return other instanceof CustomTwitchChannelEvent;
    }
}
