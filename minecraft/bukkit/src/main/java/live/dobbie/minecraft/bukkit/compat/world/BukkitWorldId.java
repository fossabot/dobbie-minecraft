package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Value
public class BukkitWorldId extends MinecraftWorldId {
    private final @Getter UUID uuid;

    public static UUID getUUID(@NonNull MinecraftWorldId worldId) {
        if(worldId instanceof BukkitWorldId) {
            return ((BukkitWorldId) worldId).getUuid();
        }
        throw new IllegalArgumentException(MinecraftWorldId.class + " must be created in " + BukkitWorldTable.class);
    }
}
