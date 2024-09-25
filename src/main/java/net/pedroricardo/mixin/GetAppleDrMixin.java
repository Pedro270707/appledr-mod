package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.content.entity.AppleDrEntity;
import net.pedroricardo.util.AppleDrConfig;
import net.pedroricardo.util.ReplacedPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(PlayerManager.class)
public class GetAppleDrMixin {
    @ModifyReturnValue(method = "getPlayer(Ljava/util/UUID;)Lnet/minecraft/server/network/ServerPlayerEntity;", at = @At(value = "RETURN", ordinal = 0))
    private ServerPlayerEntity appledrmod$getAppleDrByUUID(ServerPlayerEntity original, @Local(ordinal = 0, argsOnly = true) UUID uuid) {
        if (original == null && uuid != null && AppleDrConfig.replacedPlayers.stream().map(ReplacedPlayer::uuid).collect(Collectors.toSet()).contains(uuid)) {
            List<AppleDrEntity> list = AppleDrEntity.find(((PlayerManager)(Object) this).getServer(), aiEntity -> aiEntity instanceof AppleDrEntity appleDr && appleDr.getAssociatedPlayerUuid() != null && appleDr.getAssociatedPlayerUuid().equals(uuid));
            if (!list.isEmpty()) {
                return list.getFirst().getAsPlayer();
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getPlayer(Ljava/lang/String;)Lnet/minecraft/server/network/ServerPlayerEntity;", at = @At(value = "RETURN", ordinal = 1))
    private ServerPlayerEntity appledrmod$getAppleDrByName(ServerPlayerEntity original, @Local(ordinal = 0, argsOnly = true) String name) {
        if (original == null) {
            final String finalName = name;
            List<AppleDrEntity> list = AppleDrEntity.find(((PlayerManager)(Object) this).getServer(), appleDr -> appleDr.getGameProfile().getName().equals(finalName));
            if (!list.isEmpty()) {
                return list.getFirst().getAsPlayer();
            }
        }
        return original;
    }
}
