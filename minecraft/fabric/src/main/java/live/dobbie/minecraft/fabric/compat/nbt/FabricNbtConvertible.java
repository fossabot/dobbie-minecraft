package live.dobbie.minecraft.fabric.compat.nbt;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;

public interface FabricNbtConvertible {
    @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter);
}
