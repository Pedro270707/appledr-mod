package net.pedroricardo.util;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.entity.AppleDrEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AppleDrConfig {
    public static String openAIApiKey = null;
    public static List<ReplacedPlayer> replacedPlayers = null;
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
        replacedPlayers = AppleDrConfig.getValue("REPLACED_PLAYERS", ReplacedPlayer.CODEC.listOf(), List.of(new ReplacedPlayer(AppleDrMod.APPLEDR_UUID, AppleDrEntity.DEFAULT_PATTERN, AppleDrEntity.DEFAULT_CONTEXT)));
        appleEndAppledrness = AppleDrConfig.getValue("APPLE_END_APPLEDRNESS", Codec.INT, 200);
        aiIgnorePrefix = AppleDrConfig.getValue("AI_IGNORE_PREFIX", Codec.STRING, "AI:");
    }
}
