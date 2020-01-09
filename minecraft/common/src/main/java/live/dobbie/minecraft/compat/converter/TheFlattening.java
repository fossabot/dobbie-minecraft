package live.dobbie.minecraft.compat.converter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

// TODO map item/block/enchantment ids
public class TheFlattening {
    protected final BiMap<String, String> byOldEntity = HashBiMap.create();
    private final BiMap<String, String> byDefaultEntity = byOldEntity.inverse();

    {
        registerEntityRename("xp_orb", "experience_orb");
        registerEntityRename("xp_bottle", "experience_bottle");
        registerEntityRename("eye_of_ender_signal", "eye_of_ender");
        registerEntityRename("ender_crystal", "end_crystal");
        registerEntityRename("fireworks_rocket", "firework_rocket");
        registerEntityRename("commandblock_minecart", "command_block_minecart");
        registerEntityRename("snowman", "snow_golem");
        registerEntityRename("villager_golem", "iron_golem");
        registerEntityRename("evocation_fangs", "evoker_fangs");
        registerEntityRename("evocation_illager", "evoker");
        registerEntityRename("vindication_illager", "vindicator");
        registerEntityRename("illusion_illager", "illusioner");
    }

    private final @Getter(lazy = true)
    DefaultNameConverter defaultNameConverter = new DefaultNameConverter();
    private final @Getter(lazy = true)
    PreFlatteningNameConverter preFlatteningNameConverter = new PreFlatteningNameConverter();

    public void registerEntityRename(@NonNull String preFlatteningName, @NonNull String defaultName) {
        byOldEntity.put(preFlatteningName, defaultName);
    }

    public String getOldEntityName(@NonNull String name) {
        name = StringUtils.removeStart(name, "minecraft:");
        String oldName = byDefaultEntity.get(name);
        if (oldName != null) {
            return oldName;
        }
        return name;
    }

    public String getDefaultEntityName(@NonNull String name) {
        if (name.startsWith("minecraft:")) {
            // assuming it is already a flattened name
            // is it better to use regexp to cover mod namespaces?
            return name;
        }
        String newName = byOldEntity.get(name);
        if (newName != null) {
            return newName;
        }
        return name;
    }

    public class PreFlatteningNameConverter implements MinecraftIdConverter {
        @Override
        public @NonNull String convertEntityName(@NonNull String entityName) {
            return getOldEntityName(entityName);
        }

        @Override
        public @NonNull String convertItemId(@NonNull String id) {
            return id;
        }

        @Override
        public @NonNull String convertEnchantmentId(@NonNull String id) {
            return id;
        }
    }

    public class DefaultNameConverter implements MinecraftIdConverter {
        @Override
        public @NonNull String convertEntityName(@NonNull String entityName) {
            return getDefaultEntityName(entityName);
        }

        @Override
        public @NonNull String convertItemId(@NonNull String id) {
            return id;
        }

        @Override
        public @NonNull String convertEnchantmentId(@NonNull String id) {
            return id;
        }
    }
}
