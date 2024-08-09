package net.pedroricardo.mixin;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.content.entity.AppleDrEntity;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.AppleDrConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(PlayerAdvancementTracker.class)
public class CongratulateAdvancementMixin {
    @Mixin(PlayerAdvancementTracker.class)
    private interface OwnerAccessor {
        @Accessor("owner")
        ServerPlayerEntity owner();
    }

    @Inject(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void appledrmod$tellAppleAboutAdvancement(AdvancementEntry advancementEntry, AdvancementDisplay display, CallbackInfo ci) {
        String key = AppleDrConfig.getValue("openai_api_key", "");
        if (key.isEmpty()) return;
        final ServerPlayerEntity owner = ((OwnerAccessor) this).owner();
        new Thread(() -> {
            owner.getServer().getWorlds().forEach(world -> {
                ((EntityManagerAccessor)world).entityManager().getLookup().iterate().forEach(entity -> {
                    if (entity instanceof AppleDrEntity appleDr) {
                        try {
                            JsonObject object = AppleDrAI.sendStoredMessage(key, appleDr.getInitialMessageContext(), new AppleDrAI.Message(AppleDrAI.MessageRole.SYSTEM, String.format("The player %s received the advancement with ID %s. You should congratulate them using the name of the advancement (not the ID).", owner.getDisplayName().getString(), advancementEntry.id().toString())));
                            String message = object.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                            FakePlayer fakePlayer = FakePlayer.get(owner.getServer().getOverworld(), new GameProfile(appleDr.getGameProfile().getId(), appleDr.getGameProfile().getName()));
                            owner.getServer().getPlayerManager().broadcast(SignedMessage.ofUnsigned(message), fakePlayer, MessageType.params(MessageType.CHAT, fakePlayer));
                        } catch (IOException ignored) {
                        }
                    }
                });
            });
        }).start();
    }
}
