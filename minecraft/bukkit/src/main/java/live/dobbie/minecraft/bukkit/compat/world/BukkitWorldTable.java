package live.dobbie.minecraft.bukkit.compat.world;

import live.dobbie.minecraft.compat.world.MinecraftWorldId;
import live.dobbie.minecraft.compat.world.MinecraftWorldTable;
import lombok.NonNull;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Objects;
import java.util.function.Supplier;

public class BukkitWorldTable implements MinecraftWorldTable {
    private final @NonNull Supplier<Server> serverSupplier;
    private final BukkitWorldId overworld, theNether, theEnd;

    public BukkitWorldTable(@NonNull Supplier<Server> serverSupplier) {
        this.serverSupplier = serverSupplier;
        this.overworld = findByType(serverSupplier, World.Environment.NORMAL);
        this.theNether = findByType(serverSupplier, World.Environment.NETHER);
        this.theEnd = findByType(serverSupplier, World.Environment.THE_END);
    }

    @Override
    public @NonNull BukkitWorldId overworld() {
        return overworld;
    }

    @Override
    public @NonNull BukkitWorldId theNether() {
        return theNether;
    }

    @Override
    public @NonNull BukkitWorldId theEnd() {
        return theEnd;
    }

    @Override
    public MinecraftWorldId byName(@NonNull String name) {
        Server server = getServer(serverSupplier);
        return server.getWorlds().stream()
                .filter(world -> name.equals(world.getName()))
                .map(world -> new BukkitWorldId(world.getUID()))
                .findAny()
                .orElseGet(unknownWorld());
    }

    private static Server getServer(@NonNull Supplier<Server> serverSupplier) {
        return Objects.requireNonNull(serverSupplier.get(), "server");
    }

    private static Supplier<BukkitWorldId> unknownWorld() {
        return () -> new BukkitWorldId(null);
    }

    private static BukkitWorldId findByType(@NonNull Supplier<Server> serverSupplier, @NonNull World.Environment environment) {
        Server server = getServer(serverSupplier);
        return server.getWorlds().stream()
                .filter(world -> world.getEnvironment().equals(environment))
                .map(world -> new BukkitWorldId(world.getUID()))
                .findAny()
                .orElseGet(unknownWorld());
    }
}
