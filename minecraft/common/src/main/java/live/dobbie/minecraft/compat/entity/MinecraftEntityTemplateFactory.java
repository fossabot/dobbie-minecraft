package live.dobbie.minecraft.compat.entity;

import live.dobbie.minecraft.compat.entity.MinecraftCreeperEntityTemplate.MinecraftCreeperEntityTemplateBuilder;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate.MinecraftEntityTemplateBuilder;
import live.dobbie.minecraft.compat.entity.MinecraftZombieEntityTemplate.MinecraftZombieEntityTemplateBuilder;
import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftEntityTemplateFactory {
    public static final int DEFAULT_INT_VALUE = Integer.MIN_VALUE;
    public static final float DEFAULT_FLOAT_VALUE = Float.MIN_VALUE;

    private final @NonNull MinecraftItemInfoFactory itemInfoFactory;

    public MinecraftEntityTemplateBuilder builder() {
        return MinecraftEntityTemplate.builder();
    }

    public MinecraftEntityTemplateBuilder entityName(@NonNull String entityName) {
        return builder().entityName(entityName);
    }

    public MinecraftCreeperEntityTemplateBuilder creeper() {
        return MinecraftCreeperEntityTemplate.creeperBuilder().entityName("creeper");
    }

    public MinecraftCreeperEntityTemplateBuilder poweredCreeper() {
        return creeper().isPowered(true);
    }

    public MinecraftEntityTemplateBuilder skeleton() {
        return builder().entityName("skeleton").itemInMainHand(itemInfoFactory.id("bow"));
    }

    public MinecraftZombieEntityTemplateBuilder zombie() {
        return MinecraftZombieEntityTemplate.zombieBuilder().entityName("zombie");
    }

    public MinecraftZombieEntityTemplateBuilder babyZombie() {
        return zombie().isBaby(true);
    }

    public MinecraftEntityTemplateBuilder spider() {
        return builder().entityName("spider");
    }
}
