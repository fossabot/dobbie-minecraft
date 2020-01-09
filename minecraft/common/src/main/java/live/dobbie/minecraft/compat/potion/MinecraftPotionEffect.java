package live.dobbie.minecraft.compat.potion;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MinecraftPotionEffect {
    private final @NonNull String effectName;

    private final int durationTicks;

    @Builder.Default
    private final int amplifier = 0;

    @Builder.Default
    private final boolean showParticles = true;
}
