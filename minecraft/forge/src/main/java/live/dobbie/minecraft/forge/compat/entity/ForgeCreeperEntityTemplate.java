package live.dobbie.minecraft.forge.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftCreeperEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundNBT;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

@SuperBuilder
public class ForgeCreeperEntityTemplate extends MinecraftCreeperEntityTemplate implements ForgeEntityNbtConvertible {
    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        CompoundNBT tag = ForgeEntityNbtConvertible.build(this, converter);
        if (getExplosionRadius() != DEFAULT_INT_VALUE) {
            tag.putInt("ExplosionRadius", getExplosionRadius());
        }
        if (getFuseTicksTime() != DEFAULT_INT_VALUE) {
            tag.putInt("Fuse", getFuseTicksTime());
        }
        if (isIgnited()) {
            tag.putInt("ignited", 1);
        }
        if (isPowered()) {
            tag.putInt("powered", 1);
        }
        return tag;
    }
}
