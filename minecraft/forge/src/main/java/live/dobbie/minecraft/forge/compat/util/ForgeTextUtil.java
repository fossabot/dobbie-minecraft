package live.dobbie.minecraft.forge.compat.util;

import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.util.text.ITextComponent;

@UtilityClass
public class ForgeTextUtil {
    public ITextComponent jsonToNative(@NonNull String jsonText) {
        return ITextComponent.Serializer.fromJson(jsonText);
    }

    public ITextComponent legacyToNative(@NonNull String legacyText) {
        return jsonToNative(TextUtil.toJsonText(legacyText));
    }
}
