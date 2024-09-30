package net.pedroricardo.mixin;

import carpet.patches.EntityPlayerMPFake;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.AppleDrConfig;
import net.pedroricardo.util.ReplacedPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
public class AppleDrDisconnectMixin {
    @WrapOperation(method = "cleanUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$cancelBroadcast(PlayerManager instance, Text message, boolean overlay, Operation<Void> original) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object) this).getPlayer();
        if (!(player instanceof EntityPlayerMPFake) && !(player instanceof FakePlayer) && !AppleDrConfig.replacedPlayers.containsKey(player.getUuid())) {
            original.call(instance, message, overlay);
        }
    }

    @Inject(method = "cleanUp", at = @At("TAIL"))
    private void appledrmod$summonNewAppleDr(CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object) this).getPlayer();
        Optional<ReplacedPlayer> replacedPlayer = Optional.ofNullable(AppleDrConfig.replacedPlayers.get(player.getUuid()));
        if (!(player instanceof EntityPlayerMPFake) && !(player instanceof FakePlayer) && replacedPlayer.isPresent()) {
            player.getServerWorld().getChunkManager().addTicket(ChunkTicketType.PLAYER, player.getChunkPos(), 3, player.getChunkPos());
            AppleDrAI.createPlayer(player, player.getServer(), replacedPlayer.get().pattern(), replacedPlayer.get().context(), replacedPlayer.get().respondWhenNear());
        }
    }
}
