package live.dobbie.minecraft.compat;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class MinecraftLocation {
    private final double x, y, z;

    @NonNull
    public abstract MinecraftLocation add(double x, double y, double z);
}
