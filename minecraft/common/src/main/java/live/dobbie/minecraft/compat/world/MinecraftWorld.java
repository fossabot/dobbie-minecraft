package live.dobbie.minecraft.compat.world;

import live.dobbie.minecraft.compat.MinecraftLocation;
import live.dobbie.minecraft.compat.UnreliableResource;
import live.dobbie.minecraft.compat.block.MinecraftBlock;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.entity.MinecraftCreeperEntityTemplate.MinecraftCreeperEntityTemplateBuilder;
import live.dobbie.minecraft.compat.entity.MinecraftEntity;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate.MinecraftEntityTemplateBuilder;
import live.dobbie.minecraft.compat.entity.MinecraftZombieEntityTemplate.MinecraftZombieEntityTemplateBuilder;
import lombok.NonNull;

public interface MinecraftWorld extends UnreliableResource {
    @NonNull Object getNativeWorld();

    @NonNull String getName();

    MinecraftBlock getBlockAt(@NonNull MinecraftLocation location);

    void setBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location);

    void placeBlockAt(@NonNull MinecraftBlockInfo blockMeta, @NonNull MinecraftLocation location);

    MinecraftEntity spawnEntity(@NonNull MinecraftEntityTemplate entityInfo, @NonNull MinecraftLocation location);

    default MinecraftBlock getBlockAt(double x, double y, double z) {
        return getBlockAt(new MinecraftLocation(x, y, z));
    }

    default MinecraftEntity spawnEntity(@NonNull MinecraftEntityTemplateBuilder entityInfoBuilder,
                                        @NonNull MinecraftLocation location) {
        return spawnEntity(entityInfoBuilder.build(), location);
    }

    default MinecraftEntity spawnEntity(@NonNull MinecraftCreeperEntityTemplateBuilder entityInfoBuilder,
                                        @NonNull MinecraftLocation location) {
        return spawnEntity(entityInfoBuilder.build(), location);
    }

    default MinecraftEntity spawnEntity(@NonNull MinecraftZombieEntityTemplateBuilder entityInfoBuilder,
                                        @NonNull MinecraftLocation location) {
        return spawnEntity(entityInfoBuilder.build(), location);
    }
}
