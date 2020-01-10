package live.dobbie.minecraft.bukkit.compat;

import live.dobbie.core.user.User;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitEntityBase;
import live.dobbie.minecraft.bukkit.compat.entity.BukkitPlayerInventory;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.util.formatting.text.adapter.bukkit.TextAdapter;
import live.dobbie.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class BukkitPlayer implements User, BukkitEntityBase, MinecraftOnlinePlayer {
    private final @NonNull BukkitServer server;
    private final @NonNull UUID uuid;
    private final @NonNull @Getter(lazy = true) BukkitPlayerInventory inventory = new BukkitPlayerInventory(this);

    public BukkitPlayer(@NonNull BukkitServer server, @NonNull Player player) {
        this(server, player.getUniqueId());
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
        return getNativePlayer().getName();
    }

    @Override
    public void sendMessage(@NonNull String message) {
        TextAdapter.sendComponent(getNativePlayer(), LegacyComponentSerializer.INSTANCE.deserialize(message));
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
        getNativePlayer().kickPlayer(message);
    }

    @Override
    public void executeCommand(@NonNull String command) {
        getServer().scheduleAndWait(() -> getServer().getNativeServer().dispatchCommand(getNativePlayer(), command));
    }
}
