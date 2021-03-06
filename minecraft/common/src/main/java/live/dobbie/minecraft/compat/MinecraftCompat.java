package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfoTable;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityDespawner;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffectFactory;
import live.dobbie.minecraft.compat.util.Vector;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategoryTable;
import live.dobbie.minecraft.compat.world.MinecraftWorldTable;
import lombok.NonNull;

public interface MinecraftCompat {
    @NonNull MinecraftServer getServer();

    @NonNull MinecraftEntityTemplateFactory getEntityTemplateFactory();

    @NonNull MinecraftPotionEffectFactory getPotionEffectFactory();

    @NonNull MinecraftItemInfoFactory getItemInfoFactory();

    @NonNull MinecraftBlockInfoTable getBlockInfoTable();

    @NonNull MinecraftIdConverter getIdConverter();

    @NonNull MinecraftInventorySlotTable getInventorySlotTable();

    @NonNull MinecraftWorldTable getWorldTable();

    @NonNull MinecraftSoundCategoryTable getSoundCategoryTable();

    @NonNull MinecraftEntityDespawner getEntityDespawner();

    default @NonNull Vector unitVector() {
        return Vector.unit();
    }

    default @NonNull Vector vector(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    default @NonNull MinecraftLocation zeroLocation() {
        return MinecraftLocation.zero();
    }

    default @NonNull MinecraftLocation location(double x, double y, double z) {
        return new MinecraftLocation(x, y, z);
    }
}
