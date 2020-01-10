package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntityBase;
import live.dobbie.minecraft.fabric.compat.FabricLocation;
import live.dobbie.minecraft.fabric.compat.FabricServer;
import live.dobbie.minecraft.fabric.compat.world.FabricWorld;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import org.apache.commons.lang3.Validate;

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
}
