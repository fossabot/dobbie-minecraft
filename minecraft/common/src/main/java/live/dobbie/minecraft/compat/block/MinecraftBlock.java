package live.dobbie.minecraft.compat.block;

import live.dobbie.minecraft.compat.MinecraftLocation;
import lombok.Data;
import lombok.NonNull;

@Data
public class MinecraftBlock {
    private final @NonNull MinecraftLocation location;
    private final @NonNull MinecraftBlockInfo meta;

    @NonNull
    public final String getName() {
        return getMeta().getName();
    }
}
