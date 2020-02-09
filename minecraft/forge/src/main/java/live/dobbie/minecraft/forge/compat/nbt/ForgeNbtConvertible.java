package live.dobbie.minecraft.forge.compat.nbt;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import lombok.NonNull;
import net.minecraft.nbt.CompoundNBT;

public interface ForgeNbtConvertible {
    @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter);
}
