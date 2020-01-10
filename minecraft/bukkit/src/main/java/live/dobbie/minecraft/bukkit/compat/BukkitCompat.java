package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.minecraft.bukkit.compat.bukkit.BukkitBlockInfoTable;
import live.dobbie.minecraft.bukkit.compat.converter.BukkitIdConverter;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntityTemplateFactory;
import live.dobbie.minecraft.bukkit.compat.item.BukkitItemInfoFactory;
import live.dobbie.minecraft.bukkit.compat.world.BukkitWorldTable;
import live.dobbie.minecraft.compat.MinecraftCompat;
import live.dobbie.minecraft.compat.block.MinecraftBlockInfoTable;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffectFactory;
import lombok.NonNull;
import org.bukkit.Server;

import java.util.function.Supplier;

public class BukkitCompat implements MinecraftCompat {
    private final @NonNull Supplier<Server> serverSupplier;

    public final BukkitServer server;
    public final BukkitItemInfoFactory items;
    public final BukkitEntityTemplateFactory entities;
    public final MinecraftPotionEffectFactory potions;
    public final BukkitBlockInfoTable blocks;
    public final MinecraftInventorySlotTable invSlots;
    public final BukkitWorldTable worlds;
    public final BukkitIdConverter idConverter;

    public BukkitCompat(@NonNull Supplier<Server> serverSupplier, @NonNull Scheduler scheduler) {
        this.serverSupplier = serverSupplier;
        this.server = new BukkitServer(this, serverSupplier, scheduler);
        this.items = new BukkitItemInfoFactory();
        this.entities = new BukkitEntityTemplateFactory(items);
        this.potions = new MinecraftPotionEffectFactory();
        this.blocks = new BukkitBlockInfoTable();
        this.invSlots = new MinecraftInventorySlotTable();
        this.worlds = new BukkitWorldTable(serverSupplier);
        this.idConverter = new BukkitIdConverter();
    }

    @Override
    public @NonNull BukkitServer getServer() {
        return server;
    }

    @Override
    public @NonNull BukkitEntityTemplateFactory getEntityTemplateFactory() {
        return entities;
    }

    @Override
    public @NonNull MinecraftPotionEffectFactory getPotionEffectFactory() {
        return potions;
    }

    @Override
    public @NonNull BukkitItemInfoFactory getItemInfoFactory() {
        return items;
    }

    @Override
    public @NonNull MinecraftBlockInfoTable getBlockInfoTable() {
        return blocks;
    }

    @Override
    public @NonNull MinecraftIdConverter getIdConverter() {
        return idConverter;
    }

    @Override
    public @NonNull MinecraftInventorySlotTable getInventorySlotTable() {
        return invSlots;
    }

    @Override
    public @NonNull BukkitWorldTable getWorldTable() {
        return worlds;
    }
}
