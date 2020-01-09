package live.dobbie.minecraft.fabric.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.minecraft.compat.MinecraftCompat;
import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.converter.TheFlattening;
import live.dobbie.minecraft.compat.inventory.MinecraftInventorySlotTable;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffectFactory;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntityTemplateFactory;
import live.dobbie.minecraft.fabric.compat.item.FabricItemInfoFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class FabricCompat implements MinecraftCompat, Scheduler {
    public final @Delegate(types = Scheduler.class)
    FabricServer server;

    public final FabricEntityTemplateFactory entities;
    public final FabricItemInfoFactory items = new FabricItemInfoFactory();
    public final MinecraftPotionEffectFactory potions = new MinecraftPotionEffectFactory();
    public final FabricBlockInfoTable blocks = new FabricBlockInfoTable();
    public final MinecraftInventorySlotTable invSlots = new MinecraftInventorySlotTable();

    public final TheFlattening theFlattening = new TheFlattening();
    public final MinecraftIdConverter idConverter = theFlattening.getDefaultNameConverter();

    public FabricCompat(@NonNull Supplier<MinecraftServer> serverSupplier) {
        this.entities = new FabricEntityTemplateFactory(items);
        this.server = new FabricServer(this, serverSupplier);
    }

    @Override
    public @NonNull FabricServer getServer() {
        return server;
    }

    public @NonNull FabricEntityTemplateFactory getEntityTemplateFactory() {
        return entities;
    }

    @Override
    public @NonNull MinecraftPotionEffectFactory getPotionEffectFactory() {
        return potions;
    }

    @Override
    public @NonNull FabricItemInfoFactory getItemInfoFactory() {
        return items;
    }

    @Override
    public @NonNull FabricBlockInfoTable getBlockInfoTable() {
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
}
