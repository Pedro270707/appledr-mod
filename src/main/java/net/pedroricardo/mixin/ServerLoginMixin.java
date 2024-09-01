package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.content.entity.FakeAppleDrPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(ServerConfigurationNetworkHandler.class)
public class ServerLoginMixin {
    @WrapOperation(method = "onReady", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;getPlayer(Ljava/util/UUID;)Lnet/minecraft/server/network/ServerPlayerEntity;"))
    private ServerPlayerEntity appledrmod$letBrotherAppleJoin(PlayerManager instance, UUID uuid, Operation<ServerPlayerEntity> original) {
        ServerPlayerEntity player = original.call(instance, uuid);
        if (player instanceof FakeAppleDrPlayer appleDr) {
            return null;
        }
        return player;
    }
}
