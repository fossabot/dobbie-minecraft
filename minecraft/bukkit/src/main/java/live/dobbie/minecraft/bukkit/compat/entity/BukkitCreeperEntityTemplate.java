package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftCreeperEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

@SuperBuilder
public class BukkitCreeperEntityTemplate extends MinecraftCreeperEntityTemplate implements BukkitEntitySpawner {
    @Override
    public @NonNull Creeper spawnAndProcess(@NonNull World world, @NonNull Location location, @NonNull MinecraftIdConverter converter) {
        Creeper creeper = (Creeper) BukkitEntitySpawner.process(this, world, location, converter);
        creeper.setPowered(isPowered());
        if(getExplosionRadius() != DEFAULT_INT_VALUE) {
            creeper.setExplosionRadius(getExplosionRadius());
        }
        if(getFuseTicksTime() != DEFAULT_INT_VALUE) {
            creeper.setMaxFuseTicks(getFuseTicksTime());
        }
        if(isIgnited()) {
            creeper.ignite();
        }
        return creeper;
    }
}
