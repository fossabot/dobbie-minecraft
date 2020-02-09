package live.dobbie.minecraft.forge.compat.block;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ForgeBlockInfo extends MinecraftBlockInfo {
    private final @NonNull
    @Getter
    BlockState blockState;

    public ForgeBlockInfo(@NonNull BlockState blockState) {
        super(
                Registry.BLOCK.getKey(blockState.getBlock()).toString(),
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
        if (blockInfo instanceof ForgeBlockInfo) {
            return ((ForgeBlockInfo) blockInfo).getBlockState();
        }
        throw new IllegalArgumentException(MinecraftBlockInfo.class + " must be created using " + ForgeCompat.class);
    }
}
