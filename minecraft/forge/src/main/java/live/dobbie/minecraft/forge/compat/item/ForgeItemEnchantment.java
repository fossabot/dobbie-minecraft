package live.dobbie.minecraft.forge.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment;
import live.dobbie.minecraft.forge.compat.nbt.ForgeNbtConvertible;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundNBT;

@SuperBuilder
public class ForgeItemEnchantment extends MinecraftItemEnchantment implements ForgeNbtConvertible {
    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", getId());
        tag.putInt("lvl", getLvl());
        return tag;
    }
}
