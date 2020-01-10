package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.minecraft.compat.MinecraftLocation;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;

public class BukkitLocation extends MinecraftLocation {
    private final @NonNull
    @Getter
    Location location;

    public BukkitLocation(@NonNull Location location) {
        super(location.getX(), location.getY(), location.getZ());
        this.location = location;
    }

    public static Location getLocation(@NonNull MinecraftLocation location) {
        if (location instanceof BukkitLocation) {
            return ((BukkitLocation) location).getLocation();
        }
        return new Location(null, location.getX(), location.getY(), location.getZ());
    }
}
