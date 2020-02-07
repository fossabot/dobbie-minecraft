package live.dobbie.minecraft.fabric.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftWorldTable;
import lombok.NonNull;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class FabricWorldTable implements MinecraftWorldTable {
    private final FabricWorldId
            overworld = new FabricWorldId(DimensionType.OVERWORLD),
            theNether = new FabricWorldId(DimensionType.THE_NETHER),
            theEnd = new FabricWorldId(DimensionType.THE_END);

    @Override
    public @NonNull FabricWorldId overworld() {
        return overworld;
    }

    @Override
    public @NonNull FabricWorldId theNether() {
        return theNether;
    }

    @Override
    public @NonNull FabricWorldId theEnd() {
        return theEnd;
    }

    @Override
    public FabricWorldId byName(@NonNull String name) {
        return Registry.DIMENSION_TYPE.getOrEmpty(new Identifier(name))
                .map(FabricWorldId::new)
                .orElse(null);
    }
}
