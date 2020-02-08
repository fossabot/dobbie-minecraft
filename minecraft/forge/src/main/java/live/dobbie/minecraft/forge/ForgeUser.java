package live.dobbie.minecraft.forge;

import live.dobbie.core.user.User;
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import live.dobbie.minecraft.forge.compat.entity.player.ForgePlayer;
import live.dobbie.minecraft.forge.compat.util.ForgeTextUtil;
import lombok.NonNull;
import net.minecraft.entity.player.PlayerEntity;

// use equals and hashCode of ForgePlayer
public class ForgeUser extends ForgePlayer implements User {

    public ForgeUser(@NonNull ForgeCompat instance, @NonNull PlayerEntity entity) {
        super(instance, entity);
    }

    @Override
    public void sendErrorMessage(@NonNull String message) {
        scheduleAndWait(() -> {
            sendMessage("§c---");
            sendMessage("§c");
            sendMessage("§c[Dobbie] " + message);
            sendMessage("§c");
            sendMessage("§c---");
        });
    }

    @Override
    public void disconnect(@NonNull String message) {
        scheduleAndWait(() -> getNativePlayer().connection.disconnect(ForgeTextUtil.legacyToNative(message)));
    }
}
