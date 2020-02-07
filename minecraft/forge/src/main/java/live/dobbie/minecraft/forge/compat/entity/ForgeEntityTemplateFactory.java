package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import live.dobbie.minecraft.forge.compat.entity.ForgeCreeperEntityTemplate.ForgeCreeperEntityTemplateBuilder;
import live.dobbie.minecraft.forge.compat.entity.ForgeEntityTemplate.ForgeEntityTemplateBuilder;
import live.dobbie.minecraft.forge.compat.entity.ForgeZombieEntityTemplate.ForgeZombieEntityTemplateBuilder;
import lombok.NonNull;

public class ForgeEntityTemplateFactory extends MinecraftEntityTemplateFactory {
    public ForgeEntityTemplateFactory(@NonNull MinecraftItemInfoFactory itemInfoFactory) {
        super(itemInfoFactory);
    }

    @Override
    public ForgeEntityTemplateBuilder builder() {
        return ForgeEntityTemplate.builder();
    }

    @Override
    public ForgeCreeperEntityTemplateBuilder creeper() {
        return ForgeCreeperEntityTemplate.builder().entityName("creeper");
    }

    @Override
    public ForgeZombieEntityTemplateBuilder zombie() {
        return ForgeZombieEntityTemplate.builder().entityName("zombie");
    }
}
