package live.dobbie.minecraft.compat.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinecraftItemEnchantment {
    @NonNull String id;
    short lvl;
}
