package live.dobbie.minecraft.fabric;

import live.dobbie.util.formatting.text.serializer.gson.GsonComponentSerializer;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.minecraft.text.Text;

public class FabricUtil {
    public static Text toNativeText(@NonNull String legacyText) {
        return Text.Serializer.fromJson(toJsonText(legacyText));
    }

    public static String toJsonText(@NonNull String legacyText) {
        return GsonComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.legacy().deserialize(legacyText));
    }

    private FabricUtil() {
    }
}
