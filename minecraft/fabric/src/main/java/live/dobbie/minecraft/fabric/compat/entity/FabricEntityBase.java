package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntityBase;
import live.dobbie.minecraft.compat.util.Vector;
import live.dobbie.minecraft.fabric.compat.FabricLocation;
import live.dobbie.minecraft.fabric.compat.FabricServer;
import live.dobbie.minecraft.fabric.compat.world.FabricWorld;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public interface FabricEntityBase extends MinecraftEntityBase {
    @NonNull Entity getNativeEntity();

    @Override
    @NonNull FabricServer getServer();

    @Override
    default @NonNull String getName() {
        return getNativeEntity().getEntityName();
    }

    @Override
    default @NonNull FabricWorld getWorld() {
        return getServer().getWorldByType(getNativeEntity().getEntityWorld().getDimension().getType());
    }

    @Override
    default @NonNull FabricLocation getLocation() {
        return new FabricLocation(getNativeEntity().getBlockPos());
    }

    @Override
    default boolean isAlive() {
        return getNativeEntity().isAlive();
    }

    @Override
    default void kill() {
        getNativeEntity().kill();
    }

    @Override
    default float getHealth() {
        Entity entity = getNativeEntity();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getHealth();
        }
        return 0.0f;
    }

    @Override
    default float getMaxHealth() {
        Entity entity = getNativeEntity();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getMaximumHealth();
        }
        return 0.0f;
    }

    @Override
    default void setHealth(float health) {
        Entity entity = getNativeEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setHealth(health);
        }
    }

    @Override
    default void setMaxHealth(float maxHealth) {
        Entity entity = getNativeEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            EntityAttributeInstance attribute = livingEntity.getAttributes().get(EntityAttributes.MAX_HEALTH);
            Validate.notNull(attribute, "attribute \"" + EntityAttributes.MAX_HEALTH.getId() + "\"").setBaseValue(maxHealth);
        }
    }

    @NotNull
    @Override
    default Vector getVelocity() {
        Vec3d velocity = getNativeEntity().getVelocity();
        return new Vector(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    @Override
    default void setVelocity(@NonNull Vector vector) {
        getNativeEntity().setVelocity(new Vec3d(vector.getX(), vector.getY(), vector.getZ()));
        getNativeEntity().velocityModified = true;
    }

    @Override
    default float getYaw() {
        return getNativeEntity().getYaw(1.f);
    }

    @Override
    default float getPitch() {
        return getNativeEntity().getPitch(1.f);
    }
}
