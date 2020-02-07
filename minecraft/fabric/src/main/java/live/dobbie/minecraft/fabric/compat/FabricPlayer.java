package live.dobbie.minecraft.fabric.compat;

import live.dobbie.core.scheduler.Scheduler;
import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import live.dobbie.minecraft.fabric.compat.entity.FabricEntityBase;
import live.dobbie.minecraft.fabric.compat.entity.FabricPlayerInventory;
import live.dobbie.minecraft.fabric.compat.util.FabricTextUtil;
import live.dobbie.minecraft.fabric.compat.world.FabricSoundCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.Supplier;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class FabricPlayer implements User, MinecraftOnlinePlayer, FabricEntityBase, Scheduler {
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
        scheduleAndWait(() -> getNativePlayer().sendMessage(FabricTextUtil.legacyToNative(message)));
    }

    @Override
    public void sendRawMessage(@NonNull String rawMessage) {
        scheduleAndWait(() -> getNativePlayer().sendMessage(FabricTextUtil.jsonToNative(rawMessage)));
    }

    @Override
    public void sendTitle(String title, String subtitle, int ticksFadeIn, int ticksStay, int ticksFadeOut) {
        scheduleAndWait(() -> {
            ServerPlayNetworkHandler nh = getNativePlayer().networkHandler;
            nh.sendPacket(new TitleS2CPacket(ticksFadeIn, ticksStay, ticksFadeOut));
            nh.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, FabricTextUtil.legacyToNative(title)));
            nh.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, FabricTextUtil.legacyToNative(subtitle)));
        });
    }

    @Override
    public void sendActionBar(@NonNull String message) {
        scheduleAndWait(() -> {
            ServerPlayNetworkHandler nh = getNativePlayer().networkHandler;
            nh.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, FabricTextUtil.legacyToNative(message)));
        });
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
        scheduleAndWait(() -> getNativePlayer().networkHandler.disconnect(FabricTextUtil.legacyToNative(message)));
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.info("Dispatching command as " + this + ": \"" + command + "\"");
        scheduleAndWait(() -> {
            LOGGER.info("Executing command as " + this + ": \"" + command + "\"");
            getServer().getNativeServer().getCommandManager().execute(getNativePlayer().getCommandSource(), command);
        });
    }

    @Override
    public void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory category, float volume, float pitch) {
        scheduleAndWait(() -> {
            getNativePlayer().playSound(
                    new SoundEvent(new Identifier(sound)),
                    FabricSoundCategory.toNative(category),
                    volume,
                    pitch
            );
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

    @Override
    public void schedule(Runnable r) {
        getServer().schedule(r);
    }

    @Override
    public <V> V scheduleAndWait(Supplier<V> supplier) {
        return getServer().scheduleAndWait(supplier);
    }
}
