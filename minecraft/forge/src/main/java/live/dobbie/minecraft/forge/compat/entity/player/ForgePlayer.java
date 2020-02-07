package live.dobbie.minecraft.forge.compat.entity.player;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import live.dobbie.minecraft.forge.compat.ForgeCompat;
import live.dobbie.minecraft.forge.compat.ForgeServer;
import live.dobbie.minecraft.forge.compat.entity.ForgeEntityBase;
import live.dobbie.minecraft.forge.compat.world.ForgeSoundCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.UUID;

import static live.dobbie.minecraft.forge.compat.util.ForgeTextUtil.jsonToNative;
import static live.dobbie.minecraft.forge.compat.util.ForgeTextUtil.legacyToNative;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class ForgePlayer implements MinecraftOnlinePlayer, ForgeEntityBase, Scheduler {
    private static final ILogger LOGGER = Logging.getLogger(ForgePlayer.class);

    private final @NonNull
    @Getter
    @Delegate(types = Scheduler.class)
    ForgeCompat instance;
    private final @NonNull UUID uuid;
    private final @NonNull
    @Getter(lazy = true)
    ForgePlayerInventory inventory = new ForgePlayerInventory(this);

    public ForgePlayer(@NonNull ForgeCompat instance, @NonNull PlayerEntity entity) {
        this(instance, entity.getUniqueID());
    }

    @Override
    public void sendMessage(@NonNull String message) {
        getNativePlayer().sendMessage(legacyToNative(message));
    }

    @Override
    public void sendRawMessage(@NonNull String rawMessage) {
        getNativePlayer().sendMessage(jsonToNative(rawMessage));
    }

    @Override
    public void sendTitle(String message, String subtitle, int ticksFadeIn, int ticksStay, int ticksFadeOut) {
        scheduleAndWait(() -> {
            ServerPlayNetHandler connection = getNativePlayer().connection;
            connection.sendPacket(new STitlePacket(ticksFadeIn, ticksStay, ticksFadeOut));
            connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE, legacyToNative(message)));
            connection.sendPacket(new STitlePacket(STitlePacket.Type.SUBTITLE, legacyToNative(message)));
        });
    }

    @Override
    public void sendActionBar(@NonNull String message) {
        scheduleAndWait(() -> {
            ServerPlayNetHandler nh = getNativePlayer().connection;
            nh.sendPacket(new STitlePacket(STitlePacket.Type.ACTIONBAR, legacyToNative(message)));
        });
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.info("Dispatching command as " + this + ": \"" + command + "\"");
        scheduleAndWait(() -> {
            LOGGER.info("Executing command as " + this + ": \"" + command + "\"");
            getServer().getNativeServer().getCommandManager().handleCommand(getNativePlayer().getCommandSource(), command);
        });
    }

    @Override
    public void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory category, float volume, float pitch) {
        scheduleAndWait(() -> {
            getNativePlayer().playSound(
                    new SoundEvent(new ResourceLocation(sound)),
                    ForgeSoundCategory.toNative(category),
                    volume,
                    pitch
            );
        });
    }

    @Override
    public @NonNull ServerPlayerEntity getNativePlayer() {
        return getNativePlayerUnreliably();
    }

    private ServerPlayerEntity getNativePlayerUnreliably() {
        return instance.getServer().getNativeServer().getPlayerList().getPlayerByUUID(uuid);
    }

    @Override
    public @NonNull ServerPlayerEntity getNativeEntity() {
        return getNativePlayer();
    }

    @Override
    public @NonNull ForgeServer getServer() {
        return instance.getServer();
    }

    @Override
    public @NonNull String getUUID() {
        return uuid.toString();
    }

    @Override
    public boolean isAvailable() {
        return getServer().isAvailable() && getNativePlayerUnreliably() != null;
    }
}
