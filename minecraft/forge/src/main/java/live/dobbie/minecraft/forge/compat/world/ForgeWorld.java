package live.dobbie.minecraft.forge.compat.world;

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
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import live.dobbie.minecraft.forge.compat.ForgeLocation;
import live.dobbie.minecraft.forge.compat.block.ForgeBlockInfo;
import live.dobbie.minecraft.forge.compat.entity.ForgeEntity;
import live.dobbie.minecraft.forge.compat.entity.ForgeEntityNbtConvertible;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "dimensionType")
public class ForgeWorld implements MinecraftWorld, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(ForgeWorld.class);

    private final @NonNull
    @Delegate(types = Scheduler.class)
    ForgeCompat instance;
    private final @NonNull DimensionType dimensionType;

    public ForgeWorld(@NonNull ForgeCompat instance, @NonNull ServerWorld world) {
        this(instance, world.getDimension().getType());
    }

    @Override
    public @NonNull ServerWorld getNativeWorld() {
        return getNativeWorldUnreliably();
    }

    private ServerWorld getNativeWorldUnreliably() {
        return instance.getServer().getNativeServer().getWorld(dimensionType);
    }

    @Override
    public @NonNull String getName() {
        return scheduleAndWait(() -> getNativeWorld().dimension.getType().getSuffix());
    }

    @Override
    public MinecraftBlock getBlockAt(@NonNull MinecraftLocation location) {
        return scheduleAndWait(() -> {
            BlockState blockState = getNativeWorld().getBlockState(ForgeLocation.getBlockPos(location));
            return new MinecraftBlock(location, new ForgeBlockInfo(blockState));
        });
    }

    @Override
    public void setBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        schedule(() -> {
            BlockState blockState = ForgeBlockInfo.getBlockState(blockMeta);
            BlockPos blockPos = ForgeLocation.getBlockPos(location);
            getNativeWorld().setBlockState(blockPos, blockState);
        });
    }

    @Override
    public void placeBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location) {
        // TODO placeBlockAt not implemented
        setBlockAt(blockMeta, location);
    }

    @Override
    public ForgeEntity spawnEntity(@NonNull MinecraftEntityTemplate entityTemplate, @NonNull MinecraftLocation location) {
        if (!(entityTemplate instanceof ForgeEntityNbtConvertible)) {
            throw new IllegalArgumentException("entityTemplate cannot be converted to NBT; please use " + ForgeCompat.class);
        }
        ResourceLocation identifier = ResourceLocation.tryCreate(entityTemplate.getEntityName());
        if (identifier == null) {
            LOGGER.warning("Could not parse entity identifier \"" + entityTemplate.getEntityName() + "\"");
            return null;
        }
        ServerWorld nativeWorld = getNativeWorld();
        return scheduleAndWait(() -> {
            if (EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(identifier)) {
                LightningBoltEntity lightningEntity = new LightningBoltEntity(nativeWorld, location.getX(), location.getY(), location.getZ(), false);
                nativeWorld.addLightningBolt(lightningEntity);
                return null;
            }
            CompoundNBT compoundNBT = ((ForgeEntityNbtConvertible) entityTemplate).toCompoundNBT(instance.getIdConverter());
            Entity nativeEntity = EntityType.func_220335_a(compoundNBT, nativeWorld, (vehicleEntity) -> {
                vehicleEntity.setPosition(location.getX(), location.getY(), location.getZ());
                return nativeWorld.addEntity(vehicleEntity) ? vehicleEntity : null;
            });
            if (nativeEntity == null) {
                throw new RuntimeException("could not load entity from template " + entityTemplate);
            }
            if (nativeEntity instanceof MobEntity) {
                ((MobEntity) nativeEntity).onInitialSpawn(nativeWorld,
                        nativeWorld.getDifficultyForLocation(new BlockPos(nativeEntity)),
                        SpawnReason.COMMAND, // we probably spawned by a Dobbie command
                        null,
                        null
                );
            }
            ForgeEntity entity = new ForgeEntity(instance, nativeEntity);
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
                ForgeLocation.getBlockPos(location),
                new SoundEvent(new ResourceLocation(sound)),
                ForgeSoundCategory.toNative(soundCategory),
                volume,
                pitch
        );
    }

    @Override
    public boolean isAvailable() {
        return instance.getServer().isAvailable() && getNativeWorldUnreliably() != null;
    }
}
