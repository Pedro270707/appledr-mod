package net.pedroricardo.content.entity;

import dev.langchain4j.data.message.UserMessage;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.util.AppleDrAI;

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

        if (this.mob.getServer() == null) {
            this.messageBeingAnswered = null;
            return;
        }

        this.messageBeingAnswered = answeredMessages.getLast();
        new Thread(() -> {
            ServerPlayerEntity player = this.mob.getServer().getPlayerManager().getPlayer(this.messageBeingAnswered.getSender());
            String name;
            if (player == null) {
                name = "Unknown player: ";
            } else {
                name = String.format("%s (%s Appledrness): ", player.getName().getString(), Appledrness.getAppledrness(this.mob.getWorld(), player));
            }

            AppleDrAI.respond(this.mob.getServer(), UserMessage.userMessage(name + this.messageBeingAnswered.getContent().getString()), this.mob);
            this.messageBeingAnswered = null;
        }).start();
    }
}
