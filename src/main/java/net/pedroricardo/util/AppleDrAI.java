package net.pedroricardo.util;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import dev.langchain4j.agent.tool.*;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.entity.AIEntity;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.content.entity.AppleDrEntity;
import net.pedroricardo.mixin.EntityManagerAccessor;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AppleDrAI {
    public static final ComponentKey<AIEntityComponent> COMPONENT = ComponentRegistry.getOrCreate(Identifier.of(AppleDrMod.MOD_ID, "ai"), AIEntityComponent.class);

    public static final Map<Entity, ChatMemory> CHAT_MEMORY_MAP = new HashMap<>();
    public static final OpenAiChatModel MODEL = OpenAiChatModel.builder().apiKey(AppleDrConfig.openAIApiKey).modelName(OpenAiChatModelName.GPT_4_O_MINI).build();

    public static AiMessage respondSilently(MinecraftServer server, ChatMessage message, Entity entity) {
        final ChatMemory memory = CHAT_MEMORY_MAP.computeIfAbsent(entity, e -> new TokenWindowChatMemory.Builder().maxTokens(100000, new OpenAiTokenizer()).build());
        memory.add(message);
        List<ChatMessage> list = Lists.newArrayList(SystemMessage.systemMessage(entity.getComponent(COMPONENT).getContext()));
        list.addAll(memory.messages());
        Object tools = entity instanceof AIEntity aiEntity ? aiEntity.getTools(server) : null;
        AiMessage aiMessage;
        if (tools == null) {
            aiMessage = MODEL.generate(list).content();
            memory.add(aiMessage);
            return aiMessage;
        }
        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(tools);
        aiMessage = MODEL.generate(list, toolSpecifications).content();
        if (aiMessage.hasToolExecutionRequests()) {
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            memory.add(aiMessage);
            list.add(aiMessage);

            toolExecutionRequests.forEach(toolExecutionRequest -> {
                ToolExecutor toolExecutor = new DefaultToolExecutor(tools, toolExecutionRequest);
                String result = toolExecutor.execute(toolExecutionRequest, UUID.randomUUID().toString());
                ToolExecutionResultMessage toolExecutionResultMessages = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                memory.add(toolExecutionResultMessages);
                list.add(toolExecutionResultMessages);
            });
        }
        AiMessage finalResponse = MODEL.generate(list).content();
        memory.add(finalResponse);
        return finalResponse;
    }

    public static AiMessage respond(MinecraftServer server, ChatMessage message, Entity entity) {
        AiMessage response = respondSilently(server, message, entity);
        String str = response.text();
        FakePlayer player = entity instanceof AIEntity aiEntity ? aiEntity.getAsPlayer() : FakePlayer.get((ServerWorld) entity.getWorld(), new GameProfile(entity.getUuid(), entity.getName().getString()));
        server.getPlayerManager().broadcast(SignedMessage.ofUnsigned(str), player, MessageType.params(MessageType.CHAT, player));
        return response;
    }

    public static void reply(Entity entity, SignedMessage message) {
        if (!entity.getComponent(COMPONENT).shouldRespond()) return;
        new Thread(() -> {
            ServerPlayerEntity player = entity.getServer().getPlayerManager().getPlayer(message.getSender());
            String name;
            if (player == null) {
                name = "Unknown player: ";
            } else {
                name = String.format("%s: ", player.getName().getString());
            }

            AppleDrAI.respond(entity.getServer(), UserMessage.userMessage(name + message.getContent().getString()), entity);
        }).start();
    }

    public static List<Entity> findAIEntities(MinecraftServer server, Predicate<Entity> predicate) {
        List<Entity> list = new ArrayList<>();
        server.getWorlds().forEach(world -> {
            for (Entity entity : ((EntityManagerAccessor) world).entityManager().getLookup().iterate()) {
                if (entity.getComponent(COMPONENT).shouldRespond() && predicate.test(entity)) {
                    list.add(entity);
                }
            }
        });
        return list;
    }

    public static void create(Entity entity, Pattern pattern, String context) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            entity = new AppleDrEntity(serverPlayer.getServerWorld(), serverPlayer);
        }
        AIEntityComponent component = entity.getComponent(COMPONENT);
        component.setPattern(pattern);
        component.setContext(context);
        component.setShouldRespond(true);
    }

    public static void removeAI(Entity entity) {
        entity.getComponent(COMPONENT).setShouldRespond(false);
    }
}
