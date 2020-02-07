package live.dobbie.minecraft.fabric.compat;

import live.dobbie.minecraft.compat.MinecraftLocation;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FabricLocation extends MinecraftLocation {
    private final @Getter
    BlockPos blockPos;

    public FabricLocation(@NonNull BlockPos blockPos) {
        super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        this.blockPos = blockPos;
    }

    @Override
    public @NonNull FabricLocation add(double x, double y, double z) {
        return new FabricLocation(blockPos.add(x, y, z));
    }

    public static BlockPos getBlockPos(@Nullable MinecraftLocation location) {
        if (location == null) {
            return null;
        }
        if (location instanceof FabricLocation) {
            return ((FabricLocation) location).getBlockPos();
        }
        throw new IllegalArgumentException(MinecraftLocation.class + " must be created using " + FabricCompat.class);
    }
}
