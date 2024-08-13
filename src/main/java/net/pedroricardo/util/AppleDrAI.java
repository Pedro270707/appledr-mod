package net.pedroricardo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppleDrAI {
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";
    public static final List<Message> STORED_MESSAGES = new ArrayList<>();

    /**
     * Sends a list of messages.
     * It does not include the initial context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param messages the list of messages to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static OpenAIResponse sendMessages(String apiKey, List<Message> messages) throws IOException {
        JsonObject object;

        URL url = URI.create(API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        String payload = getPayload(messages);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            object = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
        } else {
            throw new IOException("Request failed with response code " + responseCode);
        }

        connection.disconnect();
        return OpenAIResponse.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow();
    }

    /**
     * Sends a single message.
     * Unlike {@link AppleDrAI#sendSingleMessage(String, Message, Message)}, this stores the message for future context.
     * It also includes the context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param message the message to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static OpenAIResponse sendStoredMessage(String apiKey, @Nullable Message context, Message message) throws IOException {
        STORED_MESSAGES.add(message);
        List<Message> list = new ArrayList<>();
        if (context != null) list.add(context);
        List<Message> storedMessages = STORED_MESSAGES;
        if (STORED_MESSAGES.size() > 50) {
            storedMessages = STORED_MESSAGES.subList(STORED_MESSAGES.size() - 50, STORED_MESSAGES.size());
        }
        list.addAll(storedMessages);

        OpenAIResponse response = sendMessages(apiKey, list);

        STORED_MESSAGES.add(new Message(MessageRole.ASSISTANT, response.choices().getFirst().message().content()));
        return response;
    }

    /**
     * Sends a single message. This does not store previous messages, but includes the context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param message the message to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static OpenAIResponse sendSingleMessage(String apiKey, Message context, @Nullable Message message) throws IOException {
        return sendMessages(apiKey, message == null ? List.of(context) : List.of(context, message));
    }

    private static String getPayload(List<Message> messages) {
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("model", "gpt-4o-mini");
        JsonArray messageListJson = new JsonArray();
        messages.forEach(message -> messageListJson.add(message.toJson()));
        payloadJson.add("messages", messageListJson);
        payloadJson.addProperty("max_tokens", 50);

        return payloadJson.toString();
    }

    public record Message(MessageRole role, String content, Optional<String> refusal) {
        public static final Codec<Message> CODEC = RecordCodecBuilder.create(instance -> instance.group(MessageRole.CODEC.fieldOf("role").forGetter(Message::role), Codec.STRING.fieldOf("content").forGetter(Message::content), Codec.STRING.optionalFieldOf("refusal").forGetter(Message::refusal)).apply(instance, Message::new));

        public Message(MessageRole role, String content) {
            this(role, content, Optional.empty());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("role", this.role().asString());
            json.addProperty("content", this.content);
            return json;
        }
    }

    public enum MessageRole implements StringIdentifiable {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant");

        public static final Codec<MessageRole> CODEC = StringIdentifiable.createCodec(MessageRole::values);

        private final String id;

        MessageRole(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }
}
