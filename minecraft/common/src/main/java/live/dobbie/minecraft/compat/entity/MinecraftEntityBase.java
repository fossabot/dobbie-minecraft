package live.dobbie.minecraft.compat.entity;

import live.dobbie.minecraft.compat.MinecraftLocation;
import live.dobbie.minecraft.compat.MinecraftServer;
import live.dobbie.minecraft.compat.UnreliableResource;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfo;
import live.dobbie.minecraft.compat.inventory.MinecraftInventory;
import live.dobbie.minecraft.compat.util.Vector;
import live.dobbie.minecraft.compat.world.MinecraftWorld;
import lombok.NonNull;


public interface MinecraftEntityBase extends UnreliableResource {
    @NonNull Object getNativeEntity();

    @NonNull MinecraftServer getServer();

    @NonNull String getName();

    @NonNull String getUUID();

    @NonNull MinecraftWorld getWorld();

    @NonNull MinecraftLocation getLocation();

    MinecraftInventory getInventory();

    boolean isAlive();

    void kill();

    float getHealth();

    float getMaxHealth();

    void setHealth(float health);

    void setMaxHealth(float maxHealth);

    float getYaw();

    float getPitch();

    @NonNull Vector getVelocity();

    void setVelocity(@NonNull Vector vector);

    default void setVelocity(double x, double y, double z) {
        setVelocity(new Vector(x, y, z));
    }

    default @NonNull Vector getDirection() {
        double rotX = getYaw();
        double rotY = getPitch();
        double xz = Math.cos(Math.toRadians(rotY));

        return new Vector(
                -xz * Math.sin(Math.toRadians(rotX)),
                -Math.sin(Math.toRadians(rotY)),
                xz * Math.cos(Math.toRadians(rotX))
        );
    }

    default boolean isKilled() {
        return !isAlive();
    }

    default void setBlockAt(@NonNull MinecraftBlockInfo blockInfo) {
        getWorld().setBlockAt(blockInfo, getLocation());
    }

    default void setBlockAt(@NonNull MinecraftBlockInfo blockInfo, double relativeX, double relativeY, double relativeZ) {
        getWorld().setBlockAt(blockInfo, getLocation().add(relativeX, relativeY, relativeZ));
    }

    default MinecraftEntity spawnAt(@NonNull MinecraftEntityTemplate entityInfo) {
        return getWorld().spawnEntity(entityInfo, getLocation());
    }

    default MinecraftEntity spawnAt(@NonNull MinecraftEntityTemplate.MinecraftEntityTemplateBuilder entityInfoBuilder) {
        return spawnAt(entityInfoBuilder.build());
    }

    default MinecraftEntity spawnAt(@NonNull MinecraftCreeperEntityTemplate.MinecraftCreeperEntityTemplateBuilder entityInfoBuilder) {
        return spawnAt(entityInfoBuilder.build());
    }

    default MinecraftEntity spawnAt(@NonNull MinecraftZombieEntityTemplate.MinecraftZombieEntityTemplateBuilder entityInfoBuilder) {
        return spawnAt(entityInfoBuilder.build());
    }
}
