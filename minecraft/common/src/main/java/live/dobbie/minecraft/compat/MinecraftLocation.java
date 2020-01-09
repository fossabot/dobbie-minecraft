package live.dobbie.minecraft.compat;

import lombok.Data;
import lombok.NonNull;

@Data
public class MinecraftLocation {
    private final double x, y, z;

    @NonNull
    public MinecraftLocation add(double x, double y, double z) {
        return new MinecraftLocation(this.x + x, this.y + y, this.z + z);
    }
}
