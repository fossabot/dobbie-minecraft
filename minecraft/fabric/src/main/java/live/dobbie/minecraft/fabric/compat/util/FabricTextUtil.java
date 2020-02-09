package live.dobbie.minecraft.fabric.compat.util;

import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.text.Text;

@UtilityClass
public class FabricTextUtil {
    public Text legacyToNative(@NonNull String legacyText) {
        return jsonToNative(TextUtil.toJsonText(legacyText));
    }

    public Text jsonToNative(@NonNull String jsonText) {
        return Text.Serializer.fromJson(jsonText);
    }
}
