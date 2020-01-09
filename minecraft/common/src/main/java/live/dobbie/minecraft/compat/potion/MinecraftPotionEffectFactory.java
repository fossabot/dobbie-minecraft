package live.dobbie.minecraft.compat.potion;

public class MinecraftPotionEffectFactory {
    public MinecraftPotionEffect.MinecraftPotionEffectBuilder builder() {
        return MinecraftPotionEffect.builder();
    }

    public MinecraftPotionEffect.MinecraftPotionEffectBuilder forUnlimitedTime() {
        return builder().durationTicks(Integer.MAX_VALUE);
    }

    public MinecraftPotionEffect.MinecraftPotionEffectBuilder forMinute() {
        return builder().durationTicks(20 * 60);
    }
}
