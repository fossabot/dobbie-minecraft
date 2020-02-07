package live.dobbie.minecraft.forge.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftWorldTable;
import lombok.NonNull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class ForgeWorldTable implements MinecraftWorldTable {
    private final ForgeWorldId
            overworld = new ForgeWorldId(DimensionType.OVERWORLD),
            theNether = new ForgeWorldId(DimensionType.THE_NETHER),
            theEnd = new ForgeWorldId(DimensionType.THE_END);

    @Override
    public @NonNull ForgeWorldId overworld() {
        return overworld;
    }

    @Override
    public @NonNull ForgeWorldId theNether() {
        return theNether;
    }

    @Override
    public @NonNull ForgeWorldId theEnd() {
        return theEnd;
    }

    @Override
    public ForgeWorldId byName(@NonNull String name) {
        return Registry.DIMENSION_TYPE.getValue(new ResourceLocation(name))
                .map(ForgeWorldId::new)
                .orElse(null);
    }
}
