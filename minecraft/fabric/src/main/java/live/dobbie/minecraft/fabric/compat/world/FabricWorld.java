package live.dobbie.minecraft.fabric.compat.world;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftLocation;
import live.dobbie.minecraft.compat.block.MinecraftBlock;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import live.dobbie.minecraft.compat.world.MinecraftWorld;
import live.dobbie.minecraft.fabric.compat.FabricCompat;
import live.dobbie.minecraft.fabric.compat.FabricLocation;
import live.dobbie.minecraft.fabric.compat.block.FabricBlockInfo;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntity;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntityNbtConvertible;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "dimensionType")
public class FabricWorld implements MinecraftWorld, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(FabricWorld.class);

    private final @NonNull
    @Delegate(types = Scheduler.class)
    FabricCompat instance;
    private final @NonNull DimensionType dimensionType;

    public FabricWorld(@NonNull FabricCompat instance, @NonNull ServerWorld world) {
        this(instance, world.getDimension().getType());
    }

    @Override
    public @NonNull ServerWorld getNativeWorld() {
        return getNativeWorldUnreliably();
    }

    private ServerWorld getNativeWorldUnreliably() {
        // not using scheduler to improve performance
        return instance.getServer().getNativeServer().getWorld(dimensionType);
    }

    @Override
    public @NonNull String getName() {
        return scheduleAndWait(() -> getNativeWorld().dimension.getType().getSuffix());
    }

    @Override
    public MinecraftBlock getBlockAt(@NonNull MinecraftLocation location) {
        return scheduleAndWait(() -> {
            BlockState blockState = getNativeWorld().getBlockState(FabricLocation.getBlockPos(location));
            return new MinecraftBlock(location, new FabricBlockInfo(blockState));
        });
    }

    @Override
    public void setBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        schedule(() -> {
            BlockState blockState = FabricBlockInfo.getBlockState(blockMeta);
            BlockPos blockPos = FabricLocation.getBlockPos(location);
            getNativeWorld().setBlockState(blockPos, blockState);
        });
    }

    @Override
    public void placeBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        // TODO placeBlockAt not implemented
        setBlockAt(blockMeta, location);
    }

    @Override
    public FabricEntity spawnEntity(@NonNull MinecraftEntityTemplate entityTemplate, @NonNull MinecraftLocation location) {
        if (!(entityTemplate instanceof FabricEntityNbtConvertible)) {
            throw new IllegalArgumentException("entityTemplate cannot be converted to NBT; please use " + FabricCompat.class);
        }
        Identifier identifier = Identifier.tryParse(entityTemplate.getEntityName());
        if (identifier == null) {
            LOGGER.warning("Could not parse entity identifier \"" + entityTemplate.getEntityName() + "\"");
            return null;
        }
        ServerWorld nativeWorld = getNativeWorld();
        return scheduleAndWait(() -> {
            if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(identifier)) {
                LightningEntity lightningEntity = new LightningEntity(nativeWorld, location.getX(), location.getY(), location.getZ(), false);
                nativeWorld.addLightning(lightningEntity);
                return null;
            }
            CompoundTag compoundTag = ((FabricEntityNbtConvertible) entityTemplate).toCompoundTag(instance.getIdConverter());
            Entity nativeEntity = EntityType.loadEntityWithPassengers(compoundTag, nativeWorld, (vehicleEntity) -> {
                vehicleEntity.setPositionAndAngles(location.getX(), location.getY(), location.getZ(), vehicleEntity.yaw, vehicleEntity.pitch);
                return nativeWorld.tryLoadEntity(vehicleEntity) ? vehicleEntity : null;
            });
            if (nativeEntity == null) {
                throw new RuntimeException("could not load entity from template " + entityTemplate);
            }
            if (nativeEntity instanceof MobEntity) {
                ((MobEntity) nativeEntity).initialize(nativeWorld, nativeWorld.getLocalDifficulty(new BlockPos(nativeEntity)),
                        SpawnType.COMMAND, // we probably spawned by a Dobbie command
                        null,
                        null
                );
            }
            FabricEntity entity = new FabricEntity(instance, nativeEntity);
            if (entityTemplate.getDespawnAfterTicks() != MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE) {
                instance.getEntityDespawner().queueDespawn(entity, entityTemplate.getDespawnAfterTicks());
            }
            return entity;
        });
    }

    @Override
    public void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory soundCategory,
                          @NonNull MinecraftLocation location, float volume, float pitch) {
        getNativeWorld().playSound(
                null,
                FabricLocation.getBlockPos(location),
                new SoundEvent(new Identifier(sound)),
                FabricSoundCategory.toNative(soundCategory),
                volume,
                pitch
        );
    }

    @Override
    public boolean isAvailable() {
        return instance.getServer().isAvailable() && getNativeWorldUnreliably() != null;
    }
}
