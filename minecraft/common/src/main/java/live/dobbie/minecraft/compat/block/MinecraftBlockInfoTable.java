package live.dobbie.minecraft.compat.block;

import lombok.NonNull;


public interface MinecraftBlockInfoTable {
    MinecraftBlockInfo findByName(@NonNull String name);

    default MinecraftBlockInfo air() {
        return findByName("air");
    }
}
