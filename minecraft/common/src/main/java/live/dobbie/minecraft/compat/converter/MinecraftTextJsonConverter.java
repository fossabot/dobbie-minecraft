package live.dobbie.minecraft.compat.converter;

import live.dobbie.util.formatting.text.serializer.gson.GsonComponentSerializer;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;

public final class MinecraftTextJsonConverter {

    public static String legacyToJsonText(@NonNull String text) {
        return GsonComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.legacy().deserialize(text));
    }

    private MinecraftTextJsonConverter() {
    }
}
