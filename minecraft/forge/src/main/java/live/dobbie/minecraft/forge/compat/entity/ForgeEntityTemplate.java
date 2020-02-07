package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundNBT;

@SuperBuilder
public class ForgeEntityTemplate extends MinecraftEntityTemplate implements ForgeEntityNbtConvertible {
    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        return ForgeEntityNbtConvertible.build(this, converter);
    }
}
