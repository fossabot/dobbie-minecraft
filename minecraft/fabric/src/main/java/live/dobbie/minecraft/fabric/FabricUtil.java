package live.dobbie.minecraft.fabric;

import live.dobbie.minecraft.compat.converter.MinecraftTextJsonConverter;
import live.dobbie.util.formatting.text.serializer.gson.GsonComponentSerializer;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.minecraft.text.Text;

public class FabricUtil {
    public static Text toNativeText(@NonNull String text) {
        return Text.Serializer.fromJson(MinecraftTextJsonConverter.legacyToJsonText(text));
    }

    private FabricUtil() {
    }
}
