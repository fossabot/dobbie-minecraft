package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.core.user.User;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntityBase;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitPlayerInventory;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.compat.world.MinecraftSoundCategory;
import live.dobbie.util.formatting.text.adapter.bukkit.TextAdapter;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class BukkitPlayer implements User, BukkitEntityBase, MinecraftOnlinePlayer {
    private static final ILogger LOGGER = Logging.getLogger(BukkitPlayer.class);

    private final @NonNull BukkitServer server;
    private final @NonNull UUID uuid;
    private final @NonNull String name;
    private final @NonNull
    @Getter(lazy = true)
    BukkitPlayerInventory inventory = new BukkitPlayerInventory(this);

    public BukkitPlayer(@NonNull BukkitServer server, @NonNull Player player) {
        this(server, player.getUniqueId(), player.getName());
    }

    public BukkitPlayer(@NonNull BukkitCompat instance, @NonNull Player player) {
        this(instance.getServer(), player);
    }

    @Override
    public @NonNull Player getNativePlayer() {
        return getNativePlayerUnsafe();
    }

    @Override
    public boolean isAvailable() {
        return getNativePlayerUnsafe() != null;
    }

    private Player getNativePlayerUnsafe() {
        Server server = this.server.getNativeServerUnsafe();
        return server == null? null : server.getPlayer(uuid);
    }

    @Override
    public @NonNull Player getNativeEntity() {
        return getNativePlayer();
    }

    @Override
    public @NonNull BukkitServer getServer() {
        return server;
    }

    @Override
    public @NonNull String getName() {
        return name;
    }

    @Override
    public void sendMessage(@NonNull String message) {
        LOGGER.debug("Sending message to " + getName() + ": \"" + message + "\"");
        TextAdapter.sendComponent(getNativePlayer(), LegacyComponentSerializer.INSTANCE.deserialize(message));
    }

    @Override
    public void sendRawMessage(@NonNull String rawMessage) {
        LOGGER.debug("Sending raw message to " + getName() + ": \"" + rawMessage + "\"");
        getNativePlayer().sendRawMessage(rawMessage);
    }

    @Override
    public void sendTitle(@NonNull String message, int ticksFadeIn, int ticksStay, int ticksFadeOut) {
        getNativePlayer().sendTitle(message, null, ticksFadeIn, ticksStay, ticksFadeOut);
    }

    @Override
    public void sendActionBar(@NonNull String message) {
        getNativePlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendErrorMessage(@NonNull String message) {
        sendMessage("§c---");
        sendMessage("§c");
        sendMessage("§c[Dobbie] " + message);
        sendMessage("§c");
        sendMessage("§c---");
    }

    @Override
    public void disconnect(@NonNull String message) {
        LOGGER.info("Kicking player " + getName());
        getNativePlayer().kickPlayer(message);
    }

    @Override
    public void executeCommand(@NonNull String command) {
        LOGGER.debug("Scheduling command as " + getName() + ": \"" + command + "\"");
        getServer().scheduleAndWait(() -> getServer().getNativeServer().dispatchCommand(getNativePlayer(), command));
    }

    @Override
    public void playSound(@NonNull String sound, @NonNull MinecraftSoundCategory category, float volume, float pitch) {
        Player nativePlayer = getNativePlayer();
        nativePlayer.playSound(nativePlayer.getLocation(), sound, volume, pitch);
    }

    @Override
    public String toString() {
        return "BukkitPlayer{" +
                "uuid=" + uuid +
                ", name=" + name +
                ", native=" + getNativePlayerUnsafe() +
                '}';
    }
}
