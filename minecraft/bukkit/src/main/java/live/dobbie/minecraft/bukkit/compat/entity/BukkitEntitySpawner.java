package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.bukkit.compat.item.BukkitItemInfo;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo.MinecraftItemInfoBuilder;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

public interface BukkitEntitySpawner {
    @NonNull Entity spawnAndProcess(@NonNull World world, @NonNull Location location, @NonNull MinecraftIdConverter converter);

    static Entity process(@NonNull MinecraftEntityTemplate entityTemplate, @NonNull World world,
                                  @NonNull Location location, @NonNull MinecraftIdConverter converter) {
        EntityType entityType = EntityType.valueOf(converter.convertEntityName(entityTemplate.getEntityName()));
        if(!entityType.isSpawnable()) {
            // TODO special case for some entities
            throw new RuntimeException("entity is not spawnable: " + entityType);
        }
        Entity entity = world.spawnEntity(location, entityType);
        if(entityTemplate.getCustomName() != null) {
            entity.setCustomName(entityTemplate.getCustomName());
            if(entityTemplate.isCustomNameVisible()) {
                entity.setCustomNameVisible(true);
            }
        }
        if(!entityTemplate.isPersistent()) {
            entity.setPersistent(false);
        }
        if(entityTemplate.isGlows()) {
            entity.setGlowing(true);
        }
        if(entityTemplate.isNoGravity()) {
            entity.setGravity(false);
        }
        if(!entityTemplate.isCanPickUpLoot()) {
            throw new RuntimeException("entity.canPickUpLoot not supported in Bukkit");
        }
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (entityTemplate.getMaxHealth() != DEFAULT_INT_VALUE) {
                BukkitEntityBase.setMaxHealth(livingEntity, entityTemplate.getMaxHealth());
            }
            if (entityTemplate.getHealth() != DEFAULT_INT_VALUE) {
                livingEntity.setHealth(entityTemplate.getHealth());
            }
            EntityEquipment equipment = livingEntity.getEquipment();
            if(equipment != null) {
                processEquipment(equipment, converter,
                        entityTemplate.getArmorHead(),
                        entityTemplate.getArmorBody(),
                        entityTemplate.getArmorLegs(),
                        entityTemplate.getArmorBoots(),
                        entityTemplate.getItemInMainHand(),
                        entityTemplate.getItemInOffHand()
                );
            }
            // TODO potion effects
        }

        if(entityTemplate.getFireTicks() != DEFAULT_INT_VALUE) {
            entity.setFireTicks(entityTemplate.getFireTicks());
        }
        if(entityTemplate.isSilent()) {
            entity.setSilent(true);
        }
        if(entityTemplate.isInvulnerable()) {
            entity.setInvulnerable(true);
        }
        if(entityTemplate.getRiding() != null) {
            if(entityTemplate.getRiding() instanceof BukkitEntityTemplate.BukkitEntityTemplateBuilder) {
                BukkitEntityTemplate vehicle = (BukkitEntityTemplate) entityTemplate.getRiding().build();
                Entity vehicleEntity = vehicle.spawnAndProcess(world, location, converter);
                vehicleEntity.addPassenger(entity);
                return vehicleEntity;
            } else {
                throw new RuntimeException(MinecraftEntityTemplate.class + " must be created in " + BukkitEntityTemplateFactory.class);
            }
        }
        return entity;
    }

    static void processEquipment(@NonNull EntityEquipment equipment, @NonNull MinecraftIdConverter converter,
                                 MinecraftItemInfoBuilder head, MinecraftItemInfoBuilder body,
                                 MinecraftItemInfoBuilder legs, MinecraftItemInfoBuilder boots,
                                 MinecraftItemInfoBuilder mainHand, MinecraftItemInfoBuilder offHand) {
        if(head != null) {
            equipment.setHelmet(BukkitItemInfo.getItemStack(head, converter));
        }
        if(body != null) {
            equipment.setChestplate(BukkitItemInfo.getItemStack(body, converter));
        }
        if(legs != null) {
            equipment.setLeggings(BukkitItemInfo.getItemStack(legs, converter));
        }
        if(boots != null) {
            equipment.setBoots(BukkitItemInfo.getItemStack(boots, converter));
        }
        if(mainHand != null) {
            equipment.setItemInMainHand(BukkitItemInfo.getItemStack(mainHand, converter));
        }
        if(offHand != null) {
            equipment.setItemInOffHand(BukkitItemInfo.getItemStack(offHand, converter));
        }
    }
}
