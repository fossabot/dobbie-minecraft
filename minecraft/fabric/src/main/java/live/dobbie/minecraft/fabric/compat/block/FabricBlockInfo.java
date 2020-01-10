package live.dobbie.minecraft.fabric.compat.block;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.fabric.compat.FabricCompat;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class FabricBlockInfo extends MinecraftBlockInfo {
    private final @NonNull @Getter BlockState blockState;

    public FabricBlockInfo(@NonNull BlockState blockState) {
        super(
                Registry.BLOCK.getId(blockState.getBlock()).toString(),
                blockState.getMaterial().isLiquid(),
                blockState.getMaterial().isSolid(),
                blockState.isAir()
        );
        this.blockState = blockState;
    }

    public static BlockState getBlockState(@Nullable MinecraftBlockInfo blockInfo) {
        if (blockInfo == null) {
            return null;
        }
        if (blockInfo instanceof FabricBlockInfo) {
            return ((FabricBlockInfo) blockInfo).getBlockState();
        }
        throw new IllegalArgumentException(MinecraftBlockInfo.class + " must be created using " + FabricCompat.class);
    }
}
