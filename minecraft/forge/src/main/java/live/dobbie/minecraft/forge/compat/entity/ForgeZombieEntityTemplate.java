package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftZombieEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundNBT;

@SuperBuilder
public class ForgeZombieEntityTemplate extends MinecraftZombieEntityTemplate implements ForgeEntityNbtConvertible {
    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        CompoundNBT tag = ForgeEntityNbtConvertible.build(this, converter);
        if (isBaby()) {
            tag.putInt("IsBaby", 1);
        }
        return tag;
    }
}
