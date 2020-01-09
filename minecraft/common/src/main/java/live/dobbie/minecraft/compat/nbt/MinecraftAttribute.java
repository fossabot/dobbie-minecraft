package live.dobbie.minecraft.compat.nbt;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinecraftAttribute {
    @NonNull String attributeName;
    @NonNull String name;
    @Builder.Default
    float amount = 0.0f;
    @NonNull Operation operation;
    @Builder.Default
    @NonNull
    UUID uuid = UUID.randomUUID();

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    public enum Operation {
        ADDITIVE(0),
        MULTIPLICATIVE_1(1),
        MULTIPLICATIVE_2(2),
        MULTIPLICATIVE_3(3);

        int id;
    }
}
