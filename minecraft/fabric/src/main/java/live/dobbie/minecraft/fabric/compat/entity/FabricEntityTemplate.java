package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundTag;

@SuperBuilder
public class FabricEntityTemplate extends MinecraftEntityTemplate implements FabricEntityNbtConvertible {
    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        return FabricEntityNbtConvertible.build(this, converter);
    }
}
