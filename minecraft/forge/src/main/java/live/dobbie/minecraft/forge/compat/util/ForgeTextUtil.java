package live.dobbie.minecraft.forge.compat.util;

import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import net.minecraft.util.text.ITextComponent;

public class ForgeTextUtil {
    public static ITextComponent jsonToNative(@NonNull String jsonText) {
        return ITextComponent.Serializer.fromJson(jsonText);
    }

    public static ITextComponent legacyToNative(@NonNull String legacyText) {
        return jsonToNative(TextUtil.toJsonText(legacyText));
    }

    private ForgeTextUtil() {
    }
}
