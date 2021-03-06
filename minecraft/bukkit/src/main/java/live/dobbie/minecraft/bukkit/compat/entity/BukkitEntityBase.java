package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.bukkit.compat.BukkitLocation;
import live.dobbie.minecraft.bukkit.compat.BukkitServer;
import live.dobbie.minecraft.bukkit.compat.world.BukkitWorld;
import live.dobbie.minecraft.compat.entity.MinecraftEntityBase;
import live.dobbie.minecraft.compat.util.Vector;
import lombok.NonNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface BukkitEntityBase extends MinecraftEntityBase {
    @Override
    @NonNull Entity getNativeEntity();

    @Override
    @NonNull BukkitServer getServer();

    @Override
    default @NonNull String getName() {
        return getNativeEntity().getName();
    }

    @Override
    default @NonNull String getUUID() {
        return getNativeEntity().getUniqueId().toString();
    }

    @Override
    default @NonNull BukkitWorld getWorld() {
        return getServer().getWorldByUUID(getNativeEntity().getWorld().getUID());
    }

    @Override
    default @NonNull BukkitLocation getLocation() {
        return new BukkitLocation(getNativeEntity().getLocation());
    }

    @Override
    default boolean isAlive() {
        return getNativeEntity().isDead();
    }

    @Override
    default void kill() {
        setHealth(0.f);
    }

    @Override
    default float getHealth() {
        Entity entity = getNativeEntity();
        if(entity instanceof LivingEntity) {
            return (float) ((LivingEntity) entity).getHealth();
        }
        return 0.f;
    }

    @Override
    default float getMaxHealth() {
        Entity entity = getNativeEntity();
        if(entity instanceof LivingEntity) {
            return getMaxHealth((LivingEntity) entity);
        }
        return 0.f;
    }

    @Override
    default void setHealth(float health) {
        Entity entity = getNativeEntity();
        if(entity instanceof LivingEntity) {
            ((LivingEntity) entity).setHealth(health);
        }
    }

    @Override
    default void setMaxHealth(float maxHealth) {
        Entity entity = getNativeEntity();
        if (entity instanceof LivingEntity) {
            setMaxHealth((LivingEntity) entity, maxHealth);
        }
    }

    @NotNull
    @Override
    default Vector getVelocity() {
        org.bukkit.util.Vector velocity = getNativeEntity().getVelocity();
        return new Vector(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    @Override
    default void setVelocity(@NonNull Vector vector) {
        org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(vector.getX(), vector.getY(), vector.getZ());
        getNativeEntity().setVelocity(velocity);
    }

    @Override
    default float getYaw() {
        return getNativeEntity().getLocation().getYaw();
    }

    @Override
    default float getPitch() {
        return getNativeEntity().getLocation().getPitch();
    }

    static float getMaxHealth(@NonNull LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            return (float) attr.getBaseValue();
        }
        return 0.f;
    }

    static void setMaxHealth(@NonNull LivingEntity entity, float maxHealth) {
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attr != null) {
            attr.setBaseValue(maxHealth);
        }
    }
}
