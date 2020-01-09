package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import live.dobbie.minecraft.fabric.compat.entity.FabricCreeperEntityTemplate.FabricCreeperEntityTemplateBuilder;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntityTemplate.FabricEntityTemplateBuilder;
import live.dobbie.minecraft.fabric.compat.entity.FabricZombieEntityTemplate.FabricZombieEntityTemplateBuilder;
import lombok.NonNull;

public class FabricEntityTemplateFactory extends MinecraftEntityTemplateFactory {
    public FabricEntityTemplateFactory(@NonNull MinecraftItemInfoFactory itemInfoFactory) {
        super(itemInfoFactory);
    }

    @Override
    public FabricEntityTemplateBuilder builder() {
        return FabricEntityTemplate.builder();
    }

    @Override
    public FabricCreeperEntityTemplateBuilder creeper() {
        return FabricCreeperEntityTemplate.builder().entityName("creeper");
    }

    @Override
    public FabricZombieEntityTemplateBuilder zombie() {
        return FabricZombieEntityTemplate.builder().entityName("zombie");
    }
}
