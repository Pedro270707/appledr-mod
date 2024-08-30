package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.entity.AppleDrEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class AppleDrConnectMixin {
    @Unique
    private boolean foundAppleDr;

    @WrapOperation(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$cancelJoinBroadcast(PlayerManager instance, Text message, boolean overlay, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ServerPlayerEntity player) {
        this.foundAppleDr = false;
        if (!(player instanceof FakePlayer) && player.getUuid().equals(AppleDrMod.APPLEDR_UUID)) {
            AppleDrEntity.find(instance.getServer()).forEach(appleDr -> {
                player.copyPositionAndRotation(appleDr);
                player.setHeadYaw(appleDr.getHeadYaw());
                player.setPitch(appleDr.getPitch());
                appleDr.discard();
                this.foundAppleDr = true;
            });
        }
        if (!this.foundAppleDr) {
            original.call(instance, message, overlay);
        }
    }
}
