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
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.entity.AIEntity;

import java.util.List;
import java.util.UUID;

public class AppleDrAI {
    public static final ChatMemory MEMORY = new TokenWindowChatMemory.Builder().maxTokens(1000000, new OpenAiTokenizer()).build();
    public static final OpenAiChatModel MODEL = OpenAiChatModel.builder().apiKey(AppleDrMod.OPENAI_API_KEY).modelName(OpenAiChatModelName.GPT_4_O_MINI).build();

    public static AiMessage respondSilently(MinecraftServer server, ChatMessage message, AIEntity aiEntity) {
        MEMORY.add(message);
        List<ChatMessage> list = Lists.newArrayList(SystemMessage.systemMessage(aiEntity.getInitialMessageContext()));
        list.addAll(MEMORY.messages());
        Object tools = aiEntity.getTools(server);
        AiMessage aiMessage;
        if (tools == null) {
            aiMessage = MODEL.generate(list).content();
            MEMORY.add(aiMessage);
            return aiMessage;
        }
        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(tools);
        aiMessage = MODEL.generate(list, toolSpecifications).content();
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

    public static AiMessage respond(MinecraftServer server, ChatMessage message, AIEntity aiEntity) {
        AiMessage response = respondSilently(server, message, aiEntity);
        String str = response.text();
        FakePlayer player = aiEntity.getAsPlayer();
        server.getPlayerManager().broadcast(SignedMessage.ofUnsigned(str), player, MessageType.params(MessageType.CHAT, player));
        return response;
    }
}
