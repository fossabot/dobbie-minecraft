package live.dobbie.minecraft.compat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

@SuperBuilder(builderMethodName = "creeperBuilder")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MinecraftCreeperEntityTemplate extends MinecraftEntityTemplate {
    @Builder.Default
    private final int
            explosionRadius = DEFAULT_INT_VALUE,
            fuseTicksTime = DEFAULT_INT_VALUE;

    @Builder.Default
    private final boolean
            isIgnited = false,
            isPowered = false;
}
