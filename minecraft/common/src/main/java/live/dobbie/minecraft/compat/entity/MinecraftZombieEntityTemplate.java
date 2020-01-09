package live.dobbie.minecraft.compat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "zombieBuilder")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MinecraftZombieEntityTemplate extends MinecraftEntityTemplate {
    @Builder.Default
    private final boolean isBaby = false;
}
