package live.dobbie.minecraft.compat;

import live.dobbie.minecraft.compat.block.MinecraftBlockInfoTable;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffectFactory;
import lombok.NonNull;

public interface MinecraftCompat {
    @NonNull MinecraftServer getServer();

    @NonNull MinecraftEntityTemplateFactory getEntityTemplateFactory();

    @NonNull MinecraftPotionEffectFactory getPotionEffectFactory();

    @NonNull MinecraftItemInfoFactory getItemInfoFactory();

    @NonNull MinecraftBlockInfoTable getBlockInfoTable();

    @NonNull MinecraftIdConverter getIdConverter();

    @NonNull MinecraftInventorySlotTable getInventorySlotTable();
}
