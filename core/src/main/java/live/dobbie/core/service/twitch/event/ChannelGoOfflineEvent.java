package live.dobbie.core.service.twitch.event;

import live.dobbie.core.service.twitch.data.TwitchChannel;
import lombok.*;

@Value
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChannelGoOfflineEvent extends CustomTwitchChannelEvent {
    public ChannelGoOfflineEvent(@NonNull TwitchChannel channel) {
        super(channel);
    }
}
