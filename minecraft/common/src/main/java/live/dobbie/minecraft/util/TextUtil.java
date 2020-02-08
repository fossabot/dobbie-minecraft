package live.dobbie.minecraft.util;

import live.dobbie.util.formatting.text.serializer.gson.GsonComponentSerializer;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;

public class TextUtil {
    public static String toJsonText(@NonNull String legacyText) {
        return GsonComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.legacy().deserialize(legacyText));
    }

    private TextUtil() {
    }
}
