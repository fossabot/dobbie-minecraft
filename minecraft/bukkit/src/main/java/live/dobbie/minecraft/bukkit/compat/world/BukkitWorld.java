package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.minecraft.bukkit.compat.BukkitLocation;
import live.dobbie.minecraft.bukkit.compat.BukkitServer;
import live.dobbie.minecraft.bukkit.compat.block.BukkitBlockInfo;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntity;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntityTemplate;
import live.dobbie.minecraft.compat.MinecraftLocation;
import live.dobbie.minecraft.compat.block.MinecraftBlock;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import live.dobbie.minecraft.compat.world.MinecraftWorld;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class BukkitWorld implements MinecraftWorld, Scheduler {
    private final @NonNull
    @Delegate(types = Scheduler.class)
    BukkitServer server;
    private final @NonNull UUID uuid;

    public BukkitWorld(@NonNull BukkitServer server, @NonNull World world) {
        this(server, world.getUID());
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
        Location nativeLocation = BukkitLocation.getLocation(location);
        return scheduleAndWait(() -> {
            Block block = getNativeWorld().getBlockAt(nativeLocation);
            return new MinecraftBlock(location, new BukkitBlockInfo(block.getBlockData()));
        });
    }

    @Override
    public void setBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        BlockData blockData = BukkitBlockInfo.getBlockData(blockMeta);
        Location nativeLocation = BukkitLocation.getLocation(location);
        scheduleAndWait(() -> {
            Block block = getNativeWorld().getBlockAt(nativeLocation);
            block.setBlockData(blockData);
        });
    }

    @Override
    public void placeBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        // TODO placeBlockAt not implemented
        setBlockAt(blockMeta, location);
    }

    @Override
    public BukkitEntity spawnEntity(@NonNull MinecraftEntityTemplate entityInfo, @NonNull MinecraftLocation location) {
        return scheduleAndWait(() -> {
            Entity nativeEntity = BukkitEntityTemplate.spawnAndProcess(
                    entityInfo,
                    getNativeWorld(),
                    BukkitLocation.getLocation(location),
                    server.getInstance().getIdConverter()
            );
            BukkitEntity entity = new BukkitEntity(server.getInstance(), new WeakReference<>(nativeEntity), nativeEntity.getUniqueId());
            if (entityInfo.getDespawnAfterTicks() != MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE) {
                server.getInstance().getEntityDespawner().queueDespawn(entity, entityInfo.getDespawnAfterTicks());
            }
            return entity;
        });
    }

    @Override
    public void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory soundCategory, @NonNull MinecraftLocation location,
                          float volume, float pitch) {
        SoundCategory bukkitSoundCategory = BukkitSoundCategory.toNative(soundCategory);
        getNativeWorld().playSound(BukkitLocation.getLocation(location), sound, bukkitSoundCategory, volume, pitch);
    }

    private World getNativeWorldUnsafe() {
        Server server = this.server.getNativeServerUnsafe();
        return server == null ? null : server.getWorld(uuid);
    }
}
