package live.dobbie.minecraft.bukkit.compat.block;

import live.dobbie.minecraft.bukkit.compat.BukkitCompat;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class BukkitBlockInfo extends MinecraftBlockInfo {
    private final @NonNull
    @Getter
    BlockData blockData;

    public BukkitBlockInfo(@NonNull BlockData blockData) {
        super(blockData.getMaterial().name().toLowerCase(), isLiquid(blockData.getMaterial()), blockData.getMaterial().isSolid(), blockData.getMaterial() == Material.AIR);
        this.blockData = blockData;
    }

    public static boolean isLiquid(@NonNull Material material) {
        return material == Material.WATER || material == Material.LAVA;
    }

    public static BlockData getBlockData(@Nullable MinecraftBlockInfo blockInfo) {
        if (blockInfo == null) {
            return null;
        }
        if (blockInfo instanceof BukkitBlockInfo) {
            return ((BukkitBlockInfo) blockInfo).getBlockData();
        }
        throw new IllegalArgumentException(MinecraftBlockInfo.class + " must be created using " + BukkitCompat.class);
    }
}
