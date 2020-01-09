package live.dobbie.minecraft.compat.entity;

import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.compat.potion.MinecraftPotionEffect;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_FLOAT_VALUE;
import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

@Data
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinecraftEntityTemplate {

    @NonNull String entityName;
    String customName;

    @Builder.Default
    int
            health = DEFAULT_INT_VALUE,
            maxHealth = DEFAULT_INT_VALUE,
            fireTicks = DEFAULT_INT_VALUE,
            despawnAfterTicks = DEFAULT_INT_VALUE;

    @Builder.Default
    boolean
            customNameVisible = true,
            persistent = false,
            glows = false,
            canPickUpLoot = true,
            silent = false,
            invulnerable = false,
            noGravity = false,
            leftHanded = false;

    @Builder.Default
    float speed = DEFAULT_FLOAT_VALUE;


    MinecraftItemInfo.MinecraftItemInfoBuilder
            itemInMainHand,
            itemInOffHand,
            armorHead,
            armorBody,
            armorLegs,
            armorBoots;


    @Singular
    List<MinecraftPotionEffect.MinecraftPotionEffectBuilder> potionEffects;

    MinecraftEntityTemplate.MinecraftEntityTemplateBuilder riding;
}
