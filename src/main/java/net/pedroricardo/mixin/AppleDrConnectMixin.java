package net.pedroricardo.mixin;

import carpet.patches.EntityPlayerMPFake;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.util.AppleDrAI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class AppleDrConnectMixin {
    @WrapOperation(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$onPlayerConnect(PlayerManager instance, Text message, boolean overlay, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ServerPlayerEntity player) {
        if (!(player instanceof EntityPlayerMPFake)) player.getComponent(AIEntityComponent.COMPONENT).setShouldRespond(false);
        if (!(player instanceof FakePlayer) && !AppleDrAI.findAIEntities(instance.getServer(), aiEntity -> aiEntity instanceof EntityPlayerMPFake appleDr && appleDr.getUuid().equals(player.getUuid()) && appleDr != player).isEmpty()) {
            original.call(instance, message, overlay);
        }
    }
}
