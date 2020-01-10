package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.minecraft.compat.MinecraftLocation;
import lombok.NonNull;
import org.bukkit.Location;

public class BukkitLocation extends MinecraftLocation {
    public BukkitLocation(@NonNull Location location) {
        super(location.getX(), location.getY(), location.getZ());
    }
}
