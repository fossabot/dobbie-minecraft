package live.dobbie.minecraft.bukkit.compat.entity;

import live.dobbie.minecraft.bukkit.compat.entity.BukkitCreeperEntityTemplate.BukkitCreeperEntityTemplateBuilder;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntityTemplate.BukkitEntityTemplateBuilder;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitZombieEntityTemplate.BukkitZombieEntityTemplateBuilder;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import lombok.NonNull;

public class BukkitEntityTemplateFactory extends MinecraftEntityTemplateFactory {
    public BukkitEntityTemplateFactory(@NonNull MinecraftItemInfoFactory itemInfoFactory) {
        super(itemInfoFactory);
    }

    @Override
    public BukkitEntityTemplateBuilder builder() {
        return BukkitEntityTemplate.builder();
    }

    @Override
    public BukkitCreeperEntityTemplateBuilder creeper() {
        return BukkitCreeperEntityTemplate.builder().entityName("creeper");
    }

    @Override
    public BukkitZombieEntityTemplateBuilder zombie() {
        return BukkitZombieEntityTemplate.builder().entityName("zombie");
    }
}
