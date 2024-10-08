package net.pedroricardo.mixin;

import carpet.patches.EntityPlayerMPFake;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.PlayerAITools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private static void test(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) {
        System.out.println(targets);
    }

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void appledrmod$sendChatMessageToAppleDrEntity(ServerPlayerEntity instance, SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, Operation<Void> original, @Local(argsOnly = true) ServerCommandSource source) {
        if (instance instanceof EntityPlayerMPFake fakePlayer) {
            new Thread(() -> {
                ChatMessage.ResponseMessage aiMessage = AppleDrAI.respondSilently(ChatMessage.UserMessage.of(params.name() + " whispers to you: " + message.content().getString()), fakePlayer, new PlayerAITools(fakePlayer, source.getServer()));
                MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, fakePlayer);
                source.sendChatMessage(SentMessage.of(SignedMessage.ofUnsigned(aiMessage.getContent())), source.getPlayer() != null && source.shouldFilterText(source.getPlayer()), parameters);
            }).start();
        } else {
            original.call(instance, message, filterMaskEnabled, params);
        }
    }
}
