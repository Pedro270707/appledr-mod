package net.pedroricardo.util;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.pedroricardo.AppleDrMod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppleDrConfig {
    public static String openAIApiKey = null;
    public static Map<UUID, ReplacedPlayer> replacedPlayers = null;
    public static Integer appleEndAppledrness = null;
    public static String aiIgnorePrefix = null;

    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T getValue(String setting, Codec<T> codec, @NotNull T fallback) {
        File file = FabricLoader.getInstance().getConfigDir().resolve("appledr.json").toFile();
        try {
            if (!file.exists() && !file.createNewFile()) {
                return fallback;
            }

            JsonElement config = new JsonObject();
            try {
                config = JsonParser.parseReader(new FileReader(file));
            } catch (IOException ignored) {
            }
            if (!config.isJsonObject()) {
                config = new JsonObject();
            }

            JsonElement element = config.getAsJsonObject().get(setting);
            DataResult<Pair<T, JsonElement>> result = codec.decode(JsonOps.INSTANCE, config.getAsJsonObject().get(setting));
            T value = fallback;
            if (element == null || result.isError()) {
                config.getAsJsonObject().add(setting, codec.encodeStart(JsonOps.INSTANCE, fallback).getOrThrow());
            } else {
                value = result.getOrThrow().getFirst();
            }
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(PRETTY_GSON.toJson(config));
            }
            return value;
        } catch (IOException ignored) {
        }
        return fallback;
    }

    public static void reload() {
        openAIApiKey = AppleDrConfig.getValue("OPENAI_API_KEY", Codec.STRING, "");
        replacedPlayers = AppleDrConfig.getValue("REPLACED_PLAYERS", ReplacedPlayer.CODEC.listOf(), List.of(new ReplacedPlayer(AppleDrMod.APPLEDR_UUID, Pattern.compile("(Apple|Domenic)", Pattern.CASE_INSENSITIVE), "You're Domenic Dvojmoc (aka AppleDr, your in-game name), a Slovenian player and Twitch streamer in a Minecraft server called AppleDr Server. The people in it do not speak Slovenian; they speak English, but if the message is in another language, you should respond in that language unless the player asks otherwise. You are in university at 20 years old learning computer science. Player messages will start with the player's name, but do NOT include your name in your response. If asked, you have the maximum Appledrness (2³¹ - 1). Your messages should contain at most 120 characters.", false))).stream().collect(Collectors.toMap(ReplacedPlayer::uuid, player -> player));
        appleEndAppledrness = AppleDrConfig.getValue("APPLE_END_APPLEDRNESS", Codec.INT, 200);
        aiIgnorePrefix = AppleDrConfig.getValue("AI_IGNORE_PREFIX", Codec.STRING, "AI:");
    }
}
