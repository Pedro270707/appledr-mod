package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.entity.AppleDrEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class AppleDrDisconnectMixin {
    @WrapOperation(method = "cleanUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$cancelBroadcast(PlayerManager instance, Text message, boolean overlay, Operation<Void> original) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object) this).getPlayer();
        if (!(player instanceof FakePlayer) && !AppleDrMod.REPLACED_PLAYERS.contains(player.getUuid())) {
            original.call(instance, message, overlay);
        }
    }

    @Inject(method = "cleanUp", at = @At("TAIL"))
    private void appledrmod$summonNewAppleDr(CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object) this).getPlayer();
        if (!(player instanceof FakePlayer) && AppleDrMod.REPLACED_PLAYERS.contains(player.getUuid())) {
            player.getServerWorld().getChunkManager().addTicket(ChunkTicketType.PLAYER, player.getChunkPos(), 3, player.getChunkPos());
            player.getWorld().spawnEntity(new AppleDrEntity(player.getServerWorld(), player));
        }
    }
}
