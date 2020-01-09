package live.dobbie.minecraft.compat.item;

import live.dobbie.minecraft.compat.nbt.MinecraftAttribute;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinecraftItemInfo {
    @NonNull String id;
    @Builder.Default
    int count = 1;
    @Singular
    List<MinecraftItemEnchantment.MinecraftItemEnchantmentBuilder> enchantments;
    String display;
    String displayColor;
    @Singular("displayLore")
    List<String> displayLoreList;
    @Singular
    List<MinecraftAttribute.MinecraftAttributeBuilder> attributeModifiers;
    boolean unbreakable;
    String skullOwner;
    @Builder.Default
    byte hideFlags = 0;
    @Builder.Default
    short pickupDelay = 0;
    @Builder.Default
    short age = -32768;
    String generation;
}
