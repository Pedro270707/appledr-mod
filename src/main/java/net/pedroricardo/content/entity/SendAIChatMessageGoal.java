package net.pedroricardo.content.entity;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.AppleDrConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendAIChatMessageGoal extends Goal {
    private final List<SignedMessage> answeredMessages;
    private SignedMessage messageBeingAnswered;
    private final AppleDrEntity mob;

    SendAIChatMessageGoal(AppleDrEntity mob) {
        this.mob = mob;
        this.answeredMessages = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        return this.answeredMessages.size() < this.mob.messagesReceived.size() && this.messageBeingAnswered == null;
    }

    @Override
    public boolean shouldContinue() {
        return this.messageBeingAnswered != null;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return super.shouldRunEveryTick();
    }

    @Override
    public void tick() {
        if (this.messageBeingAnswered != null) return;

        this.answeredMessages.add(this.mob.messagesReceived.getLast());

        String key = AppleDrConfig.getValue("openai_api_key", "");
        if (key.isEmpty() || this.mob.getServer() == null) {
            this.messageBeingAnswered = null;
            return;
        }

        this.messageBeingAnswered = answeredMessages.getLast();
        new Thread(() -> {
            PlayerEntity player = this.mob.getServer().getPlayerManager().getPlayer(this.messageBeingAnswered.getSender());
            String name;
            if (player == null) {
                name = "Unknown player: ";
            } else {
                name = String.format("%s (%s Appledrness): ", player.getName().getString(), Appledrness.getAppledrness(this.mob.getWorld(), player));
            }

            try {
                JsonObject object = AppleDrAI.sendStoredMessage(key, new AppleDrAI.Message(AppleDrAI.MessageRole.USER, name + this.messageBeingAnswered.getContent().getString()));
                String message = object.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                FakePlayer fakePlayer = FakePlayer.get(this.mob.getServer().getOverworld(), new GameProfile(AppleDrMod.APPLEDR_UUID, "AppleDr"));
                this.mob.getServer().getPlayerManager().broadcast(SignedMessage.ofUnsigned(message), fakePlayer, MessageType.params(MessageType.CHAT, fakePlayer));
                this.messageBeingAnswered = null;
            } catch (IOException ignored) {
            }
        }).start();
    }
}
