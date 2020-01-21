package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.fabric.FabricUtil;
import live.dobbie.minecraft.fabric.compat.item.FabricItemInfo;
import live.dobbie.minecraft.fabric.compat.item.FabricItemInfoFactory;
import live.dobbie.minecraft.fabric.compat.nbt.FabricNbtConvertible;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_FLOAT_VALUE;
import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

public interface FabricEntityNbtConvertible extends FabricNbtConvertible {
    @NonNull
    static CompoundTag build(@NonNull MinecraftEntityTemplate entityTemplate, @NonNull MinecraftIdConverter converter) {
        CompoundTag c = new CompoundTag();
        ListTag attributes = new ListTag();

        c.putString("id", converter.convertEntityName(entityTemplate.getEntityName()));

        if (entityTemplate.getCustomName() != null) {
            c.putString("CustomName", FabricUtil.toJsonText(entityTemplate.getCustomName()));
            if (entityTemplate.isCustomNameVisible()) {
                c.putInt("CustomNameVisible", 1);
            }
        }
        if (entityTemplate.isPersistent()) {
            c.putBoolean("PersistenceRequired", true);
        }
        if (entityTemplate.isGlows()) {
            c.putInt("Glowing", 1);
        }
        if (entityTemplate.isNoGravity()) {
            c.putBoolean("NoGravity", true);
        }
        if (!entityTemplate.isCanPickUpLoot()) {
            c.putBoolean("CanPickUpLoot", false);
        }
        if (entityTemplate.getHealth() != DEFAULT_INT_VALUE || entityTemplate.getMaxHealth() != DEFAULT_INT_VALUE) {
            if (entityTemplate.getHealth() != DEFAULT_INT_VALUE) {
                c.putInt("Health", entityTemplate.getHealth());
            }

            int maxHealth;
            if (entityTemplate.getMaxHealth() != DEFAULT_INT_VALUE) {
                maxHealth = entityTemplate.getMaxHealth();
            } else {
                maxHealth = entityTemplate.getHealth();
            }

            CompoundTag maxHealthTag = new CompoundTag();
            maxHealthTag.putString("Name", "generic.maxHealth");
            maxHealthTag.putInt("Base", maxHealth);
            attributes.add(maxHealthTag);
        }
        if (entityTemplate.getFireTicks() != DEFAULT_INT_VALUE) {
            c.putInt("Fire", entityTemplate.getFireTicks());
        }
        if (entityTemplate.getSpeed() != DEFAULT_FLOAT_VALUE) {
            CompoundTag speedTag = new CompoundTag();
            speedTag.putString("Name", "generic.movementSpeed");
            speedTag.putFloat("Base", entityTemplate.getSpeed());
            attributes.add(speedTag);
        }
        if (entityTemplate.isSilent()) {
            c.putInt("Silent", 1);
        }
        if (entityTemplate.isInvulnerable()) {
            c.putInt("Invulnerable", 1);
        }

        ListTag armorItems = new ListTag();
        boolean hasArmorItems;
        hasArmorItems = processItem(armorItems, entityTemplate.getArmorBoots(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorLegs(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorBody(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorHead(), converter);
        if (hasArmorItems) {
            c.put("ArmorItems", armorItems);
        }

        // HandItems:[{id:"minecraft:diamond_sword",Count:1},{}]
        ListTag handItems = new ListTag();
        boolean hasHandItems;
        hasHandItems = processItem(handItems, entityTemplate.getItemInMainHand(), converter);
        hasHandItems |= processItem(handItems, entityTemplate.getItemInOffHand(), converter);
        if (hasHandItems) {
            c.put("HandItems", handItems);
        }

        if (!attributes.isEmpty()) {
            c.put("Attributes", attributes);
        }

        // TODO potion effects

        if (entityTemplate.getRiding() != null) {
            ListTag passengers = new ListTag();
            passengers.add(c);

            CompoundTag vehicle;
            if(entityTemplate.getRiding() instanceof FabricEntityTemplate.FabricEntityTemplateBuilder) {
                FabricEntityTemplate riding = (FabricEntityTemplate) entityTemplate.getRiding().build();
                vehicle = riding.toCompoundTag(converter);
            } else {
                throw new RuntimeException(MinecraftEntityTemplate.class + " must be created in " + FabricEntityTemplateFactory.class);
            }
            vehicle.put("Passengers", passengers);

            return vehicle;
        }

        return c;
    }

    @NonNull
    static CompoundTag build(@NonNull MinecraftEntityTemplate.MinecraftEntityTemplateBuilder entityTemplateBuilder, @NonNull MinecraftIdConverter converter) {
        return build(entityTemplateBuilder.build(), converter);
    }

    static boolean processItem(ListTag listTag, MinecraftItemInfo.MinecraftItemInfoBuilder itemInfoBuilder, MinecraftIdConverter converter) {
        if (itemInfoBuilder == null) {
            // empty tag
            listTag.add(new CompoundTag());
            return false;
        }
        MinecraftItemInfo itemInfo = itemInfoBuilder.build();
        if (!(itemInfo instanceof FabricItemInfo)) {
            throw new RuntimeException("armor item must be created in " + FabricItemInfoFactory.class);
        }
        FabricItemInfo item = (FabricItemInfo) itemInfo;
        CompoundTag tag = item.toCompoundTag(converter);
        listTag.add(tag);
        return true;
    }
}
