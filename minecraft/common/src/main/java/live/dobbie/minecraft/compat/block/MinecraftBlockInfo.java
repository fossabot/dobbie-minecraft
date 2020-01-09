package live.dobbie.minecraft.compat.block;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinecraftBlockInfo {
    @NonNull String name;
    boolean liquid, solid, air;
}
