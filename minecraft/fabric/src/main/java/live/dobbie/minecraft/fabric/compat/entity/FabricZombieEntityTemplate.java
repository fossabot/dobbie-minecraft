package live.dobbie.minecraft.fabric.compat.entity;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.entity.MinecraftZombieEntityTemplate;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.nbt.CompoundTag;

@SuperBuilder
public class FabricZombieEntityTemplate extends MinecraftZombieEntityTemplate implements FabricEntityNbtConvertible {
    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        CompoundTag tag = FabricEntityNbtConvertible.build(this, converter);
        if (isBaby()) {
            tag.putInt("IsBaby", 1);
        }
        return tag;
    }
}
