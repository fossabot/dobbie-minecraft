package live.dobbie.core.service.twitch.data;

import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.primitive.NullPrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import live.dobbie.core.service.twitch.TwitchSource;
import lombok.NonNull;
import lombok.Value;

@Value
public class TwitchChannelPointsReward {
    @NonNull String title;
    @NonNull Price cost;
    String prompt;

    @NonNull
    public static TwitchChannelPointsReward fromTwitch4j(@NonNull ChannelPointsReward reward) {
        return new TwitchChannelPointsReward(
                reward.getTitle(),
                new Price(reward.getCost(), TwitchSource.CHANNEL_POINTS_CURRENCY),
                reward.getPrompt()
        );
    }

    public static class Title implements PrimitiveConverter<TwitchChannelPointsReward, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull TwitchChannelPointsReward value) {
            return new StringPrimitive(value.getTitle());
        }
    }

    public static class Prompt implements PrimitiveConverter<TwitchChannelPointsReward, Primitive> {
        @NonNull
        @Override
        public Primitive parse(@NonNull TwitchChannelPointsReward value) {
            return value.getPrompt() == null ? NullPrimitive.INSTANCE : new StringPrimitive(value.getPrompt());
        }
    }
}
