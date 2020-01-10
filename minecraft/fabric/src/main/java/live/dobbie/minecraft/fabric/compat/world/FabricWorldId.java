package live.dobbie.minecraft.fabric.compat.world;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import live.dobbie.minecraft.fabric.compat.FabricCompat;
import lombok.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Value
public class FabricWorldId extends MinecraftWorldId {
    private final @NonNull DimensionType dimensionType;

    public static DimensionType getDimensionType(MinecraftWorldId worldId) {
        if(worldId instanceof FabricWorldId) {
            return ((FabricWorldId) worldId).getDimensionType();
        }
        throw new IllegalArgumentException(MinecraftWorldId.class + " must be created using " + FabricWorldTable.class);
    }
}
