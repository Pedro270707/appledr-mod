package net.pedroricardo.mixin;

import carpet.patches.EntityPlayerMPFake;
import dev.langchain4j.data.message.SystemMessage;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.PlayerAITools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancementTracker.class)
public class CongratulateAdvancementMixin {
    @Mixin(PlayerAdvancementTracker.class)
    private interface OwnerAccessor {
        @Accessor("owner")
        ServerPlayerEntity owner();
    }

    @Inject(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$tellAppleAboutAdvancement(AdvancementEntry advancementEntry, AdvancementDisplay display, CallbackInfo ci) {
        final ServerPlayerEntity owner = ((OwnerAccessor) this).owner();
        new Thread(() -> {
            AppleDrAI.findAIEntities(owner.getServer(), entity -> entity instanceof EntityPlayerMPFake).forEach(aiEntity -> {
                AppleDrAI.respond(owner.getServer(), SystemMessage.systemMessage(String.format("The player %s received the advancement with ID %s. You should congratulate them using the name of the advancement (not the ID).", owner.getDisplayName().getString(), advancementEntry.id().toString())), aiEntity, new PlayerAITools((EntityPlayerMPFake) aiEntity, aiEntity.getServer()));
            });
        }).start();
    }
}
