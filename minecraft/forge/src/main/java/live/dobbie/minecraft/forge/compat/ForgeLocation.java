package live.dobbie.minecraft.forge.compat;

import live.dobbie.minecraft.compat.MinecraftLocation;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ForgeLocation extends MinecraftLocation {
    private final @NonNull
    @Getter
    BlockPos blockPos;

    public ForgeLocation(BlockPos blockPos) {
        super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        this.blockPos = blockPos;
    }

    @NonNull
    public ForgeLocation add(double x, double y, double z) {
        return new ForgeLocation(blockPos.add(x, y, z));
    }

    public static BlockPos getBlockPos(@Nullable MinecraftLocation location) {
        if (location == null) {
            return null;
        }
        if (location instanceof ForgeLocation) {
            return ((ForgeLocation) location).getBlockPos();
        }
        throw new IllegalArgumentException(MinecraftLocation.class + " must be created using " + ForgeCompat.class);
    }
}
