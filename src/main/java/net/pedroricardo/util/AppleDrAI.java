package net.pedroricardo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AppleDrAI {
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static JsonObject sendMessageRequest(String apiKey, String message) throws IOException {
        JsonObject object;

        URL url = URI.create(API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        String payload = getPayload(message);

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

    private static String getPayload(String message) {
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("model", "gpt-4o-mini");
        JsonArray messages = new JsonArray();
        JsonObject contextMessage = new JsonObject();
        contextMessage.addProperty("role", "system");
        contextMessage.addProperty("content", "You are Domenic Dvojmoc (also known as AppleDr, your in-game name), a Slovenian player and Twitch streamer who is in a Minecraft server called AppleDr Server. The people in the server do not speak Slovenian; rather, they speak English. You are in university at 20 years old.");
        messages.add(contextMessage);
        JsonObject userMessageJson = new JsonObject();
        userMessageJson.addProperty("role", "user");
        userMessageJson.addProperty("content", message);
        messages.add(userMessageJson);
        payloadJson.add("messages", messages);
        payloadJson.addProperty("max_tokens", 50);

        return payloadJson.toString();
    }
}
