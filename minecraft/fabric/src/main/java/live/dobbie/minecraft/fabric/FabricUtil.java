package live.dobbie.minecraft.fabric;

import live.dobbie.util.formatting.text.serializer.gson.GsonComponentSerializer;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.minecraft.text.Text;

public class FabricUtil {
    public static Text toNativeText(@NonNull String text) {
        return Text.Serializer.fromJson(toJsonText(text));
    }

    public static String toJsonText(@NonNull String text) {
        return GsonComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.legacy().deserialize(text));
    }

    private FabricUtil() {
    }
}
