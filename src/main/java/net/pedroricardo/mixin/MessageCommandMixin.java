package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.pedroricardo.content.entity.FakeAppleDrPlayer;
import net.pedroricardo.util.AppleDrAI;
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
        if (instance instanceof FakeAppleDrPlayer fakePlayer) {
            new Thread(() -> {
                AiMessage aiMessage = AppleDrAI.respondSilently(source.getServer(), UserMessage.userMessage(params.name() + " whispers to you: " + message.content().getString()), fakePlayer.getAppleDr());
                MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, fakePlayer);
                source.sendChatMessage(SentMessage.of(SignedMessage.ofUnsigned(aiMessage.text())), source.getPlayer() != null && source.shouldFilterText(source.getPlayer()), parameters);
            }).start();
        } else {
            original.call(instance, message, filterMaskEnabled, params);
        }
    }
}
