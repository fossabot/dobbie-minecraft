package live.dobbie.minecraft.fabric.compat;

import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntityBase;
import live.dobbie.minecraft.fabric.compat.entity.FabricPlayerInventory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static live.dobbie.minecraft.fabric.FabricUtil.toNativeText;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class FabricPlayer implements User, MinecraftOnlinePlayer, FabricEntityBase {
    private static final ILogger LOGGER = Logging.getLogger(FabricPlayer.class);

    private final @NonNull
    @Getter
    FabricCompat instance;
    private final @NonNull UUID uuid;
    private final @NonNull
    @Getter
    String name;
    private final @NonNull
    @Getter(lazy = true)
    FabricPlayerInventory inventory = new FabricPlayerInventory(this);

    public FabricPlayer(@NonNull FabricCompat instance, @NonNull ServerPlayerEntity playerEntity) {
        this(instance, playerEntity.getUuid(), playerEntity.getEntityName());
    }

    @Override
    public boolean isAvailable() {
        return instance.getServer().isAvailable() && getNativeEntityUnreliably() != null;
    }

    private ServerPlayerEntity getNativeEntityUnreliably() {
        return instance.getServer().getNativeServer().getPlayerManager().getPlayer(uuid);
    }

    @NonNull
    @Override
    public ServerPlayerEntity getNativeEntity() {
        return getNativeEntityUnreliably();
    }

    @Override
    public @NonNull FabricServer getServer() {
        return instance.getServer();
    }

    @NonNull
    @Override
    public ServerPlayerEntity getNativePlayer() {
        return getNativeEntity();
    }

    @Override
    public void sendMessage(@NonNull String message) {
        getServer().scheduleAndWait(() -> getNativePlayer().sendMessage(toNativeText(message)));
    }

    @Override
    public void sendRawMessage(@NonNull String rawMessage) {
        getServer().scheduleAndWait(() -> getNativePlayer().sendMessage(Text.Serializer.fromJson(rawMessage)));
    }

    @Override
    public void sendErrorMessage(@NonNull String message) {
        getServer().scheduleAndWait(() -> {
            sendMessage("§c---");
            sendMessage("§c");
            sendMessage("§c[Dobbie] " + message);
            sendMessage("§c");
            sendMessage("§c---");
        });
    }

    @Override
    public void disconnect(@NonNull String message) {
        getServer().scheduleAndWait(() -> getNativePlayer().networkHandler.disconnect(toNativeText(message)));
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.info("Dispatching command as " + this + ": \"" + command + "\"");
        getServer().scheduleAndWait(() -> {
            LOGGER.info("Executing command as " + this + ": \"" + command + "\"");
            getServer().getNativeServer().getCommandManager().execute(getNativePlayer().getCommandSource(), command);
        });
    }

    @Override
    public String getUUID() {
        return uuid.toString();
    }

    @Override
    public String toString() {
        return "FabricPlayer{" +
                "uuid=" + uuid +
                ", nativePlayer=" + getNativePlayer() +
                '}';
    }
}
