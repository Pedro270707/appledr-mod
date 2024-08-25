package net.pedroricardo.util;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.AppleDrStatistics;
import net.pedroricardo.content.entity.AppleDrEntity;

import java.util.List;
import java.util.UUID;

public class AppleDrAI {
    public static final ChatMemory MEMORY = new TokenWindowChatMemory.Builder().maxTokens(10000, new OpenAiTokenizer()).build();
    public static final OpenAiChatModel MODEL = OpenAiChatModel.withApiKey(AppleDrMod.OPENAI_API_KEY);

    public static AiMessage respond(MinecraftServer server, ChatMessage message, AppleDrEntity appleDr) {
        MEMORY.add(message);
        List<ChatMessage> list = Lists.newArrayList(SystemMessage.systemMessage(appleDr.getInitialMessageContext()));
        list.addAll(MEMORY.messages());
        Tools tools = new Tools(server, appleDr);
        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(tools);
        AiMessage aiMessage = MODEL.generate(list, toolSpecifications).content();
        if (aiMessage.hasToolExecutionRequests()) {
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            MEMORY.add(aiMessage);
            list.add(aiMessage);

            toolExecutionRequests.forEach(toolExecutionRequest -> {
                ToolExecutor toolExecutor = new DefaultToolExecutor(tools, toolExecutionRequest);
                String result = toolExecutor.execute(toolExecutionRequest, UUID.randomUUID().toString());
                ToolExecutionResultMessage toolExecutionResultMessages = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                MEMORY.add(toolExecutionResultMessages);
                list.add(toolExecutionResultMessages);
            });
        }
        AiMessage finalResponse = MODEL.generate(list).content();
        MEMORY.add(finalResponse);
        String str = finalResponse.text();
        FakePlayer fakePlayer = FakePlayer.get(appleDr.getServer().getOverworld(), new GameProfile(appleDr.getGameProfile().getId(), appleDr.getGameProfile().getName()));
        server.getPlayerManager().broadcast(SignedMessage.ofUnsigned(str), fakePlayer, MessageType.params(MessageType.CHAT, fakePlayer));
        return finalResponse;
    }

    static class Tools {
        private final MinecraftServer server;
        private final AppleDrEntity appleDr;

        Tools(MinecraftServer server, AppleDrEntity appleDr) {
            this.server = server;
            this.appleDr = appleDr;
        }

        @Tool("Gives the player 50 Appledrness if they haven't received it yet. Respond according to the received Appledrness.")
        String giveAppledrness(String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) > 0) {
                return playerName + " cannot receive another Appledrness boost";
            } else {
                player.incrementStat(AppleDrStatistics.APPLEDRS_GRACE);
                return "Gave " + playerName + " +50 Appledrness";
            }
        }

        @Tool("Teleports to the player unless they are in a dangerous situation.")
        String teleportTo(String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getBlockStateAtPos().isOf(Blocks.LAVA)) {
                return playerName + " is in lava";
            } else {
                this.appleDr.teleportTo(new TeleportTarget(player.getServerWorld(), player.getPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
                return "Teleported to " + playerName;
            }
        }

        @Tool("Gets the item in your hand.")
        String getMainHandStack() {
            return this.appleDr.getMainHandStack().toString();
        }

        @Tool("Gets the item in your offhand.")
        String getOffHandStack() {
            return this.appleDr.getOffHandStack().toString();
        }

        @Tool("Gets your equipped items.")
        String getEquippedItems() {
            String helmet = "Helmet: " + this.appleDr.getEquippedStack(EquipmentSlot.HEAD) + "; ";
            String chestplate = "Chestplate: " + this.appleDr.getEquippedStack(EquipmentSlot.CHEST) + "; ";
            String leggings = "Leggings: " + this.appleDr.getEquippedStack(EquipmentSlot.LEGS) + "; ";
            String boots = "Boots: " + this.appleDr.getEquippedStack(EquipmentSlot.FEET);
            return helmet + chestplate + leggings + boots;
        }

        @Tool("Gets every item in your inventory.")
        String getInventoryItems() {
            return this.appleDr.getInventory().toString();
        }
    }
}
