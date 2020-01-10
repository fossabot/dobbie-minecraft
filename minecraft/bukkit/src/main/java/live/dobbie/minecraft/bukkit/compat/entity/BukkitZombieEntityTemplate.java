package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftZombieEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Zombie;

@SuperBuilder
public class BukkitZombieEntityTemplate extends MinecraftZombieEntityTemplate implements BukkitEntitySpawner {
    @Override
    public @NonNull Zombie spawnAndProcess(@NonNull World world, @NonNull Location location, @NonNull MinecraftIdConverter converter) {
        Zombie zombie = (Zombie) BukkitEntitySpawner.process(this, world, location, converter);
        if(isBaby()) {
            zombie.setBaby(true);
        }
        return zombie;
    }
}
