package live.dobbie.minecraft.bukkit.compat.bukkit;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfoTable;
import lombok.NonNull;
import org.bukkit.Material;

public class BukkitBlockInfoTable implements MinecraftBlockInfoTable {
    @Override
    public BukkitBlockInfo findByName(@NonNull String name) {
        Material material = Material.matchMaterial(name, false);
        return material == null? null : new BukkitBlockInfo(material.createBlockData());
    }
}
