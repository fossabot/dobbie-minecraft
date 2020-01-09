package live.dobbie.minecraft.fabric.mixin;

import live.dobbie.minecraft.fabric.DobbieFabric;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerManager.class)
public abstract class PlayerJoinQuitMixin {
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void dobbie_onPlayerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo callbackInfo) {
        DobbieFabric.playerJoined(player);
    }

    @Inject(at = @At("RETURN"), method = "remove")
    private void dobbie_onPlayerDisconnected(ServerPlayerEntity player, CallbackInfo callbackInfo) {
        DobbieFabric.playerQuit(player);
    }
}
