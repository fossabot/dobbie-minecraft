package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.forge.compat.item.ForgeItemInfo;
import live.dobbie.minecraft.forge.compat.item.ForgeItemInfoFactory;
import live.dobbie.minecraft.forge.compat.nbt.ForgeNbtConvertible;
import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_FLOAT_VALUE;
import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

public interface ForgeEntityNbtConvertible extends ForgeNbtConvertible {
    @NonNull
    static CompoundNBT build(@NonNull MinecraftEntityTemplate entityTemplate, @NonNull MinecraftIdConverter converter) {
        CompoundNBT c = new CompoundNBT();
        ListNBT attributes = new ListNBT();

        c.putString("id", converter.convertEntityName(entityTemplate.getEntityName()));

        if (entityTemplate.getCustomName() != null) {
            c.putString("CustomName", TextUtil.toJsonText(entityTemplate.getCustomName()));
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

            CompoundNBT maxHealthTag = new CompoundNBT();
            maxHealthTag.putString("Name", "generic.maxHealth");
            maxHealthTag.putInt("Base", maxHealth);
            attributes.add(maxHealthTag);
        }
        if (entityTemplate.getFireTicks() != DEFAULT_INT_VALUE) {
            c.putInt("Fire", entityTemplate.getFireTicks());
        }
        if (entityTemplate.getSpeed() != DEFAULT_FLOAT_VALUE) {
            CompoundNBT speedTag = new CompoundNBT();
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

        ListNBT armorItems = new ListNBT();
        boolean hasArmorItems;
        hasArmorItems = processItem(armorItems, entityTemplate.getArmorBoots(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorLegs(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorBody(), converter);
        hasArmorItems |= processItem(armorItems, entityTemplate.getArmorHead(), converter);
        if (hasArmorItems) {
            c.put("ArmorItems", armorItems);
        }

        // HandItems:[{id:"minecraft:diamond_sword",Count:1},{}]
        ListNBT handItems = new ListNBT();
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
            ListNBT passengers = new ListNBT();
            passengers.add(c);

            CompoundNBT vehicle;
            if (entityTemplate.getRiding() instanceof ForgeEntityTemplate.ForgeEntityTemplateBuilder) {
                ForgeEntityTemplate riding = (ForgeEntityTemplate) entityTemplate.getRiding().build();
                vehicle = riding.toCompoundNBT(converter);
            } else {
                throw new RuntimeException(MinecraftEntityTemplate.class + " must be created in " + ForgeEntityTemplateFactory.class);
            }
            vehicle.put("Passengers", passengers);

            return vehicle;
        }

        return c;
    }

    @NonNull
    static CompoundNBT build(@NonNull MinecraftEntityTemplate.MinecraftEntityTemplateBuilder entityTemplateBuilder, @NonNull MinecraftIdConverter converter) {
        return build(entityTemplateBuilder.build(), converter);
    }

    static boolean processItem(ListNBT ListNBT, MinecraftItemInfo.MinecraftItemInfoBuilder itemInfoBuilder, MinecraftIdConverter converter) {
        if (itemInfoBuilder == null) {
            // empty tag
            ListNBT.add(new CompoundNBT());
            return false;
        }
        MinecraftItemInfo itemInfo = itemInfoBuilder.build();
        if (!(itemInfo instanceof ForgeItemInfo)) {
            throw new RuntimeException("armor item must be created in " + ForgeItemInfoFactory.class);
        }
        ForgeItemInfo item = (ForgeItemInfo) itemInfo;
        CompoundNBT tag = item.toCompoundNBT(converter);
        ListNBT.add(tag);
        return true;
    }
}
