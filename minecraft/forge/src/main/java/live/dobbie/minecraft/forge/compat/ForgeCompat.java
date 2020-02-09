package live.dobbie.minecraft.forge.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.minecraft.compat.MinecraftCompat;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.converter.TheFlattening;
import live.dobbie.minecraft.compat.entity.MinecraftEntityDespawner;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffectFactory;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategoryTable;
import live.dobbie.minecraft.forge.compat.block.ForgeBlockInfoTable;
import live.dobbie.minecraft.forge.compat.entity.ForgeEntityTemplateFactory;
import live.dobbie.minecraft.forge.compat.item.ForgeItemInfoFactory;
import live.dobbie.minecraft.forge.compat.world.ForgeSoundCategoryTable;
import live.dobbie.minecraft.forge.compat.world.ForgeWorldTable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class ForgeCompat implements MinecraftCompat, Scheduler {
    public final @Delegate(types = Scheduler.class)
    ForgeServer server;

    public final ForgeEntityTemplateFactory entities;
    public final ForgeItemInfoFactory items = new ForgeItemInfoFactory();
    public final MinecraftPotionEffectFactory potions = new MinecraftPotionEffectFactory();
    public final ForgeBlockInfoTable blocks = new ForgeBlockInfoTable();
    public final MinecraftInventorySlotTable invSlots = new MinecraftInventorySlotTable();
    public final ForgeWorldTable worlds = new ForgeWorldTable();
    public final ForgeSoundCategoryTable soundCats = new ForgeSoundCategoryTable();

    public final TheFlattening theFlattening = new TheFlattening();
    public final MinecraftIdConverter idConverter = theFlattening.getDefaultNameConverter();

    private final MinecraftEntityDespawner entityDespawner = new MinecraftEntityDespawner();

    public ForgeCompat(@NonNull Supplier<MinecraftServer> serverSupplier) {
        this.entities = new ForgeEntityTemplateFactory(items);
        this.server = new ForgeServer(this, serverSupplier);
    }

    @Override
    public @NonNull ForgeServer getServer() {
        return server;
    }

    public @NonNull ForgeEntityTemplateFactory getEntityTemplateFactory() {
        return entities;
    }

    @Override
    public @NonNull MinecraftPotionEffectFactory getPotionEffectFactory() {
        return potions;
    }

    @Override
    public @NonNull ForgeItemInfoFactory getItemInfoFactory() {
        return items;
    }

    @Override
    public @NonNull ForgeBlockInfoTable getBlockInfoTable() {
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
    public @NonNull ForgeWorldTable getWorldTable() {
        return worlds;
    }

    @Override
    public @NonNull MinecraftSoundCategoryTable getSoundCategoryTable() {
        return soundCats;
    }

    @Override
    public @NonNull MinecraftEntityDespawner getEntityDespawner() {
        return entityDespawner;
    }
}
