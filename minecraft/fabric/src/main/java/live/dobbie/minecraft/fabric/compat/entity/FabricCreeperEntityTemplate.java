package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftCreeperEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundTag;

import static live.dobbie.minecraft.compat.entity.MinecraftEntityTemplateFactory.DEFAULT_INT_VALUE;

@SuperBuilder
public class FabricCreeperEntityTemplate extends MinecraftCreeperEntityTemplate implements FabricEntityNbtConvertible {
    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        CompoundTag tag = FabricEntityNbtConvertible.build(this, converter);
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
