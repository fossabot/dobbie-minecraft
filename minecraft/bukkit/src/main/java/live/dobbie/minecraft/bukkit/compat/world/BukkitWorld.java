package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.minecraft.bukkit.compat.BukkitCompat;
import live.dobbie.minecraft.compat.MinecraftLocation;
import live.dobbie.minecraft.compat.block.MinecraftBlock;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.entity.MinecraftEntity;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.world.MinecraftWorld;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class BukkitWorld implements MinecraftWorld {
    private final @NonNull BukkitCompat instance;
    private final @NonNull UUID uuid;

    public BukkitWorld(@NonNull BukkitCompat instance, @NonNull World world) {
        this(instance, world.getUID());
    }

    @Override
    public @NonNull World getNativeWorld() {
        return getNativeWorldUnsafe();
    }

    @Override
    public boolean isAvailable() {
        return getNativeWorldUnsafe() != null;
    }

    @Override
    public @NonNull String getName() {
        return getNativeWorld().getName();
    }

    @Override
    public MinecraftBlock getBlockAt(@NonNull MinecraftLocation location) {
        return null;
    }

    @Override
    public void setBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {

    }

    @Override
    public void placeBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
    }

    @Override
    public MinecraftEntity spawnEntity(@NonNull MinecraftEntityTemplate entityInfo, @NonNull MinecraftLocation location) {
        return null;
    }

    private World getNativeWorldUnsafe() {
        Server server = instance.getServer().getNativeServerUnsafe();
        return server == null ? null : server.getWorld(uuid);
    }
}
