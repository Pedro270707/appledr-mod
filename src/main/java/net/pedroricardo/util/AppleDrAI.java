package net.pedroricardo.util;

import com.google.common.collect.Lists;
import dev.langchain4j.agent.tool.*;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrDimension;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;
import net.pedroricardo.content.entity.AppleDrEntity;

import java.util.List;
import java.util.UUID;

public class AppleDrAI {
    public static final ChatMemory MEMORY = new TokenWindowChatMemory.Builder().maxTokens(1000000, new OpenAiTokenizer()).build();
    public static final OpenAiChatModel MODEL = OpenAiChatModel.builder().apiKey(AppleDrMod.OPENAI_API_KEY).modelName(OpenAiChatModelName.GPT_4_O_MINI).build();

    public static AiMessage respondSilently(MinecraftServer server, ChatMessage message, AppleDrEntity appleDr) {
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
        return finalResponse;
    }

    public static AiMessage respond(MinecraftServer server, ChatMessage message, AppleDrEntity appleDr) {
        AiMessage response = respondSilently(server, message, appleDr);
        String str = response.text();
        FakePlayer player = appleDr.getAsPlayer();
        server.getPlayerManager().broadcast(SignedMessage.ofUnsigned(str), player, MessageType.params(MessageType.CHAT, player));
        return response;
    }

    static class Tools {
        private static final String[] catchphrases = new String[] {
                "An apple a day keeps the doctor away! But you're an Apple Doctor!",
                "I'm an embarrassment to society and societal norms!",
                "Feeling good. I am feeling good.",
                "I'm gonna do the iconic AppleDr dance that I learned in Bavaria!",
                "Don't say book in Slovenian!",
                "Java is pronounced \"Yava!\"",
                "If you haven't played Pogostuck yet, you should play it!"
        };

        private final MinecraftServer server;
        private final AppleDrEntity appleDr;

        Tools(MinecraftServer server, AppleDrEntity appleDr) {
            this.server = server;
            this.appleDr = appleDr;
        }

        @Tool("Gives the player 50 Appledrness if they haven't received it yet. Respond according to the received Appledrness.")
        String giveAppledrnessTo(String playerName) {
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
        String getHandStacks() {
            return "Main: " + this.appleDr.getMainHandStack().toString() + "; Off: " + this.appleDr.getOffHandStack().toString();
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

        @Tool("Gets one of your iconic catchphrases")
        String getCatchphrase() {
            return Util.getRandom(catchphrases, this.appleDr.getRandom());
        }

        @Tool("Teleports a player to/from the Apple End, a new apple dimension. Only executes at the own player's request. Usable multiple times on a single player. If asked about a custom dimension, mention this one.")
        String sendOrRemoveFromAppleEnd(@P(value = "The name of the player who asked to be teleported") String playerName) {
            System.out.println("Ran sendOrRemoveFromAppleEnd");
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            int appledrness;
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
                player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else if (Math.abs(appledrness = Appledrness.getAppledrness(player.getWorld(), player)) >= 1000) {
                EndPlatformFeature.generate(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).down(), true);
                player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " to the Apple End (reason: player " + (appledrness > 0 ? "has a high Appledrness" : "is too evil, scaring you into sending them to the Apple End") + ")";
            }
            return "The absolute value of " + playerName + "'s Appledrness is too low; not teleporting";
        }

        @Tool("Gets the Appledrness of a player and the corresponding level.")
        String getAppledrness(String playerName) {
            System.out.println("Ran getAppledrness");
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            }
            int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
            String appledrlevel = Appledrlevels.getAppledrlevel(appledrness).getId().getPath();
            appledrlevel = Character.toUpperCase(appledrlevel.charAt(0)) + appledrlevel.substring(1);
            return playerName + "'s Appledrness is " + appledrness + " (" + appledrlevel + ")";
        }

        @Tool("Takes the player to/from 1e8bf29c9c6240b2a7aae7d226df8486. Always use if the player says that exact sequence of characters without asking for a core, regardless of whether they said it before or not; otherwise, pretend the dimension DOES NOT EXIST without implying that it does, and do not run this. If the player insists that it does exist without providing the code, pretend they're talking about the Apple End and teleport them there. Reply with a message like \"1e8bf29c9c6240b2a7aae7d226df8486. Maybe.\" or \"Yes\", in a cryptic way when this is ran.")
        String sendOrRemoveFrom1e8bf29c9c6240b2a7aae7d226df8486(@P(value = "The name of the player who said the sequence of characters") String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
                player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else {
                player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486), BlockPos.ORIGIN.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
                return "Teleported " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486. Respond cryptically in an extremely serious tone.";
            }
        }

        @Tool("Gives the player a core. Should only be used if the player asks for it and says the sequence of characters 1e8bf29c9c6240b2a7aae7d226df8486; otherwise, ignore this and tell the player that you do not know what a core is.")
        String giveCore(@P(value = "The name of the player who said the sequence of characters and requested a core") String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            }
            player.giveItemStack(new ItemStack(AppleDrItems.CORE));
            return "Gave " + playerName + " 1 * Core";
        }
    }
}
