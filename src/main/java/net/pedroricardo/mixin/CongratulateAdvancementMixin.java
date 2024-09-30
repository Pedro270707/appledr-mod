package net.pedroricardo.mixin;

import carpet.patches.EntityPlayerMPFake;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Language;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.PlayerAITools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

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
        if (advancementEntry.value().display().isEmpty() || advancementEntry.value().name().isEmpty() || owner.getServer() == null) return;
        new Thread(() -> {
            AppleDrAI.findAIEntities(owner.getServer(), entity -> entity instanceof EntityPlayerMPFake).forEach(aiEntity -> {
                AppleDrAI.respond(owner.getServer(), ChatMessage.SystemMessage.of(String.format("The player %s received the advancement %s (description: %s). You should " + (owner == aiEntity ? "celebrate, since you got the advancement." : "congratulate them using the name of the advancement."), owner.getDisplayName().getString(), Localization.text(advancementEntry.value().name().get(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString(), Localization.text(advancementEntry.value().display().get().getDescription(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString())), aiEntity, new PlayerAITools((EntityPlayerMPFake) aiEntity, aiEntity.getServer()));
            });
        }).start();
    }
}
