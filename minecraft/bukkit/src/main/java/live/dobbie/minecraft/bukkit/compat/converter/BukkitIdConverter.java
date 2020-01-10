package live.dobbie.minecraft.bukkit.compat.converter;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.converter.TheFlattening;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

@RequiredArgsConstructor
public class BukkitIdConverter implements MinecraftIdConverter {
    private final @NonNull MinecraftIdConverter delegate;

    public BukkitIdConverter() {
        this(new TheFlattening().getDefaultNameConverter());
    }

    @Override
    @NonNull
    public String convertEntityName(@NonNull String entityName) {
        String converted = delegate.convertEntityName(entityName);
        converted = StringUtils.removeStart(converted, "minecraft:");
        for (EntityType value : EntityType.values()) {
            if (value == EntityType.UNKNOWN) {
                // Entity.UNKNOWN.getKey() will throw an exception
                continue;
            }
            NamespacedKey key = value.getKey();
            if (key.getKey().equals(converted)) {
                return value.name();
            }
        }
        throw new IllegalArgumentException("could not find entity \"" + entityName + "\" (converted as \"" + converted + "\") in Bukkit's" + EntityType.class);
    }

    @Override
    @NonNull
    public String convertItemId(@NonNull String id) {
        return delegate.convertItemId(id);
    }

    @Override
    @NonNull
    public String convertEnchantmentId(@NonNull String id) {
        return delegate.convertEnchantmentId(id);
    }
}
