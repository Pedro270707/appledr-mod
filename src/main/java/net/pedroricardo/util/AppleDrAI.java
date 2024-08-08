package net.pedroricardo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.StringIdentifiable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AppleDrAI {
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";
    public static final Message INITIAL_CONTEXT = new Message(MessageRole.SYSTEM, "You're Domenic Dvojmoc (aka AppleDr, your in-game name), a Slovenian player and Twitch streamer in a Minecraft server called AppleDr Server. The people in it do not speak Slovenian; they speak English, but if the message is in another language, you should respond in that language unless the player asks otherwise. You are in university at 20 years old learning computer science. Player messages will start with some information about the player, such as their name and their Appledrness, but do NOT include that in your response. If asked, you have the maximum Appledrness (2³¹ - 1). Your messages should contain at most 120 characters.");
    public static final List<Message> STORED_MESSAGES = new ArrayList<>() {
        {
            this.add(INITIAL_CONTEXT);
        }
    };

    /**
     * Sends a list of messages.
     * It does not include the initial context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param messages the list of messages to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static JsonObject sendMessages(String apiKey, List<Message> messages) throws IOException {
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
        return object;
    }

    /**
     * Sends a single message.
     * Unlike {@link AppleDrAI#sendSingleMessage(String, Message)}, this stores the message for future context.
     * It also includes the context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param message the message to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static JsonObject sendStoredMessage(String apiKey, Message message) throws IOException {
        STORED_MESSAGES.add(message);
        return sendMessages(apiKey, STORED_MESSAGES);
    }

    /**
     * Sends a single message. This does not store previous messages, but includes the context that the AI is AppleDr.
     * @param apiKey the OpenAI API key
     * @param message the message to send to the AI
     * @return the response as a JSON object
     * @throws IOException if an I/O issue happens
     */
    public static JsonObject sendSingleMessage(String apiKey, Message message) throws IOException {
        return sendMessages(apiKey, List.of(INITIAL_CONTEXT, message));
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

    public record Message(MessageRole role, String content) {
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
