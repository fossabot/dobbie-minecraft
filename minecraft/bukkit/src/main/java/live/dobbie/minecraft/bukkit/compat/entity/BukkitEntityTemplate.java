package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@SuperBuilder
public class BukkitEntityTemplate extends MinecraftEntityTemplate implements BukkitEntitySpawner {
    @Override
    public @NonNull Entity spawnAndProcess(@NonNull World world, @NonNull Location location, @NonNull MinecraftIdConverter converter) {
        return BukkitEntitySpawner.process(this, world, location, converter);
    }
}
