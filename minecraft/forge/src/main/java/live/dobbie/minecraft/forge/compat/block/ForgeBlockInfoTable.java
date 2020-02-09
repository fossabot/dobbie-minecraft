package live.dobbie.minecraft.forge.compat.block;

import com.google.common.collect.ImmutableMap;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfoTable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@RequiredArgsConstructor
public class ForgeBlockInfoTable implements MinecraftBlockInfoTable {
    private static final ILogger LOGGER = Logging.getLogger(ForgeBlockInfoTable.class);

    @Override
    public @Nullable ForgeBlockInfo findByName(@NonNull String name) {
        Block block;
        Optional<Block> blockOptional = Registry.BLOCK.getValue(ResourceLocation.tryCreate(name));
        if (blockOptional.isPresent()) {
            block = blockOptional.get();
        } else {
            LOGGER.warning("Could not find block: " + name);
            return null;
        }
        return new ForgeBlockInfo(new BlockState(block, ImmutableMap.of()));
    }
}
