package live.dobbie.minecraft.compat.nbt;

import lombok.Getter;
import lombok.NonNull;

public class MinecraftAttributeFactory {
    @Getter
    public final MinecraftAttribute
            maxHealth = newAttr("generic.maxHealth", 20.f),
            knockbackResistance = newAttr("generic.knockbackResistance", 0.f),
            movementSpeed = newAttr("generic.movementSpeed", 0.1f /* of player */),
            attackDamage = newAttr("generic.attackDamage", 1.f),
            armor = newAttr("generic.armor", 0.f),
            armorToughness = newAttr("generic.armorToughness", 0.f),
            attackKnockback = newAttr("generic.attackKnockback", 1.5f);

    public MinecraftAttribute.MinecraftAttributeBuilder builder() {
        return MinecraftAttribute.builder();
    }

    private MinecraftAttribute newAttr(@NonNull String name, float defaultBase) {
        return MinecraftAttribute.builder().name(name).attributeName(name).amount(defaultBase).build();
    }
}
