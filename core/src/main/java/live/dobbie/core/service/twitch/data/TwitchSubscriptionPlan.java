package live.dobbie.core.service.twitch.data;

import com.github.twitch4j.chat.enums.SubscriptionPlan;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
public class TwitchSubscriptionPlan {
    @NonNull Tier tier;

    public enum Tier {
        UNKNOWN("Unknown subscription tier"),
        TWITCH_PRIME("Twitch Prime tier"),
        TIER_1("Twitch Tier-1"),
        TIER_2("Twitch Tier-2"),
        TIER_3("Twitch Tier-3");

        private final @Getter
        String locName;

        Tier(String locName) {
            this.locName = locName;
        }

        public static Tier from(SubscriptionPlan plan) {
            if (plan != null) {
                switch (plan) {
                    case TWITCH_PRIME:
                        return TWITCH_PRIME;
                    case TIER1:
                        return TIER_1;
                    case TIER2:
                        return TIER_2;
                    case TIER3:
                        return TIER_3;
                }
            }
            return UNKNOWN;
        }
    }
}
