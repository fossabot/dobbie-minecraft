package live.dobbie.minecraft.forge.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.minecraft.world.dimension.DimensionType;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Value
public class ForgeWorldId extends MinecraftWorldId {
    private final @NonNull DimensionType dimensionType;

    public static DimensionType getDimensionType(MinecraftWorldId worldId) {
        if (worldId instanceof ForgeWorldId) {
            return ((ForgeWorldId) worldId).getDimensionType();
        }
        throw new IllegalArgumentException(MinecraftWorldId.class + " must be created using " + ForgeWorldTable.class);
    }
}
