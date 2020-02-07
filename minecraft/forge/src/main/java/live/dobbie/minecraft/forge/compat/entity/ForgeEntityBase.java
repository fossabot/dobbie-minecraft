package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntityBase;
import live.dobbie.minecraft.compat.util.Vector;
import live.dobbie.minecraft.forge.compat.ForgeLocation;
import live.dobbie.minecraft.forge.compat.ForgeServer;
import live.dobbie.minecraft.forge.compat.world.ForgeWorld;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public interface ForgeEntityBase extends MinecraftEntityBase {
    @NonNull Entity getNativeEntity();

    @Override
    @NonNull ForgeServer getServer();

    @Override
    default @NonNull String getName() {
        return getNativeEntity().getName().getFormattedText();
    }

    @Override
    default @NonNull ForgeWorld getWorld() {
        return getServer().getWorldByType(getNativeEntity().getEntityWorld().getDimension().getType());
    }

    @Override
    default @NonNull ForgeLocation getLocation() {
        return new ForgeLocation(getNativeEntity().getPosition());
    }

    @Override
    default boolean isAlive() {
        return getNativeEntity().isAlive();
    }

    @Override
    default void kill() {
        getNativeEntity().remove();
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
            return ((LivingEntity) entity).getMaxHealth();
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
            IAttributeInstance attribute = livingEntity.getAttributes().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
            Validate.notNull(attribute, "attribute \"" + SharedMonsterAttributes.MAX_HEALTH + "\"").setBaseValue(maxHealth);
        }
    }

    @NotNull
    @Override
    default Vector getVelocity() {
        Vec3d velocity = getNativeEntity().getMotion();
        return new Vector(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    @Override
    default void setVelocity(@NonNull Vector vector) {
        getNativeEntity().setMotion(new Vec3d(vector.getX(), vector.getY(), vector.getZ()));
        getNativeEntity().velocityChanged = true;
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
