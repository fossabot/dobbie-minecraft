package live.dobbie.minecraft.fabric.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment;
import live.dobbie.minecraft.fabric.compat.nbt.FabricNbtConvertible;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundTag;

@SuperBuilder
public class FabricItemEnchantment extends MinecraftItemEnchantment implements FabricNbtConvertible {
    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId());
        tag.putInt("lvl", getLvl());
        return tag;
    }
}
